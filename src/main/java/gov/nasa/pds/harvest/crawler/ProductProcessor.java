package gov.nasa.pds.harvest.crawler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;

import org.w3c.dom.Document;

import gov.nasa.pds.harvest.cfg.model.BundleCfg;
import gov.nasa.pds.harvest.cfg.model.Configuration;
import gov.nasa.pds.harvest.dao.RegistryManager;
import gov.nasa.pds.harvest.util.out.SupplementalWriter;
import gov.nasa.pds.harvest.util.out.WriterManager;
import gov.nasa.pds.registry.common.meta.Metadata;
import gov.nasa.pds.registry.common.util.xml.XmlDomUtils;
import gov.nasa.pds.registry.common.util.xml.XmlNamespaces;


/**
 * Process products (PDS4 XML label files) excluding collections and bundles.
 *
 * <p> Processing steps:
 * <ul>
 * <li>Crawl file system</li>
 * <li>Parse PDS4 labels (XML files)</li>
 * <li>Extract product metadata</li>
 * <li>Write extracted metadata into a JSON or XML file.</li> 
 * <li>Generated JSON files can be imported into Elasticsearch by Registry manager tool.</li>
 * </ul>
 *  
 * @author karpenko
 */
public class ProductProcessor extends BaseProcessor
{
    /**
     * Constructor
     * @param config Harvest configuration parameters
     * @throws Exception Generic exception
     */
    public ProductProcessor(Configuration config) throws Exception
    {
        super(config);
    }

    
    /**
     * Inner class used by Files.find() to select product label files.
     * @author karpenko
     */
    private static class FileMatcher implements BiPredicate<Path, BasicFileAttributes>
    {
        private Set<String> includeDirs;
        private int startIndex;
        
        
        /**
         * Constructor
         * @param bCfg Bundle configuration
         */
        public FileMatcher(BundleCfg bCfg)
        {
            this.includeDirs = bCfg.productDirs;
        
            File rootDir = new File(bCfg.dir);
            String rootPath = rootDir.toPath().toUri().toString();
            startIndex = rootPath.length();
        }

        
        @Override
        public boolean test(Path path, BasicFileAttributes attrs)
        {
            String fileName = path.getFileName().toString().toLowerCase();
            if(!fileName.endsWith(".xml")) return false;

            if(includeDirs == null) return true;
            String fileDir = path.getParent().toUri().toString().toLowerCase();
            
            for(String dir: includeDirs)
            {
                // Search the relative path only
                if(fileDir.indexOf(dir, startIndex) >= 0) return true;
            }
            
            return false;
        }
    }

    
    /**
     * Process products of a bundle
     * @param bCfg Bundle configuration
     * @throws Exception Generic exception
     */
    public void process(BundleCfg bCfg) throws Exception
    {
        log.info("Processing products...");

        FileMatcher matcher = new FileMatcher(bCfg);
        
        File bundleDir = new File(bCfg.dir);
        Iterator<Path> it = Files.find(bundleDir.toPath(), 20, matcher).iterator();
        
        while(it.hasNext())
        {
            onFile(it.next().toFile());
        }
    }
    
    
    /**
     * Process one file
     * @param file PDS label file
     * @throws Exception Generic exception
     */
    public void onFile(File file) throws Exception
    {
        Document doc = null;
        Counter counter = RegistryManager.getInstance().getCounter();
        
        try
        {
            // Skip very large files
            if(file.length() > MAX_XML_FILE_LENGTH)
            {
                log.warn("File is too big to parse: " + file.getAbsolutePath());
                counter.skippedFileCount++;
                return;
            }

            doc = XmlDomUtils.readXml(dbf, file);
        }
        catch(Exception ex)
        {
            log.warn(ex.getMessage());
            counter.failedFileCount++;
            return;
        }        
        
        String rootElement = doc.getDocumentElement().getNodeName();
        
        // Ignore collections and bundles
        if("Product_Bundle".equals(rootElement) || "Product_Collection".equals(rootElement)) return;

        // Apply product filter
        if(config.filters.prodClassInclude != null)
        {
            if(!config.filters.prodClassInclude.contains(rootElement)) return;
        }
        else if(config.filters.prodClassExclude != null)
        {
            if(config.filters.prodClassExclude.contains(rootElement)) return;
        }

        // Process metadata
        try
        {
            processMetadata(file, doc);
        }
        catch(Exception ex)
        {
            log.error(ex.getMessage());
            counter.failedFileCount++;
        }        
    }
    
    
    /**
     * Extract metadata from a label file.
     * @param file PDS label file
     * @param doc XML DOM model of the PDS label file
     * @throws Exception Generic exception
     */
    private void processMetadata(File file, Document doc) throws Exception
    {
        Counter counter = RegistryManager.getInstance().getCounter();
        
        // Extract basic metadata
        Metadata meta = basicExtractor.extract(file, doc);
        meta.setNodeName(config.nodeName);

        // Only process primary products from collection inventory
        LidVidCache cache = RefsCache.getInstance().getProdRefsCache();
        if(!cache.containsLidVid(meta.lidvid) && !cache.containsLid(meta.lid)) 
        {
            log.info("Skipping product " + file.getAbsolutePath() + " (LIDVID/LID is not in collection inventory or is already registered in Elasticsearch)");
            counter.skippedFileCount++;
            return;
        }
        
        log.info("Processing product " + file.getAbsolutePath());

        // Internal references
        refExtractor.addRefs(meta.intRefs, doc);
        
        // Extract fields by XPath
        xpathExtractor.extract(doc, meta.fields);

        // Extract fields autogenerated from data dictionary
        XmlNamespaces nsInfo = autogenExtractor.extract(file, meta.fields);

        // Search fields
        searchExtractor.extract(doc, meta.fields);
        
        // Extract file data
        fileDataExtractor.extract(file, meta, config.fileInfo.fileRef);
        
        // Save metadata
        save(meta, nsInfo);
        
        // Process supplemental products
        String rootElement = doc.getDocumentElement().getNodeName();
        if(rootElement.equals("Product_Metadata_Supplemental"))
        {
            SupplementalWriter swriter = WriterManager.getInstance().getSupplementalWriter();
            swriter.write(file);
        }
    }

}
