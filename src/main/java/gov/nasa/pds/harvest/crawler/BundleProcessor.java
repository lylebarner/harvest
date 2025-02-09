package gov.nasa.pds.harvest.crawler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

import org.w3c.dom.Document;

import gov.nasa.pds.harvest.cfg.model.BundleCfg;
import gov.nasa.pds.harvest.cfg.model.Configuration;
import gov.nasa.pds.harvest.dao.RegistryManager;
import gov.nasa.pds.harvest.dao.RegistryDao;
import gov.nasa.pds.registry.common.meta.BundleMetadataExtractor;
import gov.nasa.pds.registry.common.meta.Metadata;
import gov.nasa.pds.registry.common.util.xml.XmlDomUtils;
import gov.nasa.pds.registry.common.util.xml.XmlNamespaces;


/**
 * <p>Process "Product_Bundle" products (PDS4 XML label files).
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
public class BundleProcessor extends BaseProcessor
{
    private BundleMetadataExtractor bundleExtractor;
    
    private int bundleCount;
    private BundleCfg bundleCfg;
    
    
    /**
     * Constructor
     * @param config Harvest configuration parameters
     * @param counter document / product counter
     * @throws Exception Generic exception
     */
    public BundleProcessor(Configuration config) throws Exception
    {
        super(config);

        bundleExtractor = new BundleMetadataExtractor();
    }
    
    
    /**
     * Inner class used by Files.find() to select bundle label files.
     * @author karpenko
     */
    private static class BundleMatcher implements BiPredicate<Path, BasicFileAttributes>
    {
        @Override
        public boolean test(Path path, BasicFileAttributes attrs)
        {
            String fileName = path.getFileName().toString().toLowerCase();
            return (fileName.endsWith(".xml") && fileName.contains("bundle"));
        }
    }

    
    /**
     * Process one bundle configuration from Harvest configuration file.
     * @param bCfg Bundle configuration (directory, version, etc.)
     * @return Number of bundles processed (O or more)
     * @throws Exception Generic exception
     */
    public int process(BundleCfg bCfg) throws Exception
    {
        bundleCount = 0;
        this.bundleCfg = bCfg;
        
        File bundleDir = new File(bCfg.dir);
        Iterator<Path> it = Files.find(bundleDir.toPath(), 1, new BundleMatcher()).iterator();
        while(it.hasNext())
        {
            onBundle(it.next().toFile());
        }

        return bundleCount;
    }

    
    /**
     * Process one bundle label file.
     * @param file PDS XML Label file
     * @throws Exception Generic exception
     */
    private void onBundle(File file) throws Exception
    {
        // Skip very large files
        if(file.length() > MAX_XML_FILE_LENGTH)
        {
            log.warn("File is too big to parse: " + file.getAbsolutePath());
            return;
        }

        Document doc = XmlDomUtils.readXml(dbf, file);
        String rootElement = doc.getDocumentElement().getNodeName();
        
        // Ignore non-bundle XMLs
        if(!"Product_Bundle".equals(rootElement)) return;
        
        processMetadata(file, doc);
    }
    
    
    private void processMetadata(File file, Document doc) throws Exception
    {
        Metadata meta = basicExtractor.extract(file, doc);
        meta.setNodeName(config.nodeName);
        
        if(bundleCfg.versions != null && !bundleCfg.versions.contains(meta.strVid)) return;

        log.info("Processing bundle " + file.getAbsolutePath());
        bundleCount++;
        
        RegistryDao dao = RegistryManager.getInstance().getRegistryDao(); 
        Counter counter = RegistryManager.getInstance().getCounter();
        
        // Bundle already registered in the Registry (Elasticsearch)
        if(dao.idExists(meta.lidvid))
        {
            log.warn("Bundle " + meta.lidvid + " already registered. Skipping.");
            addCollectionRefs(meta, doc);
            counter.skippedFileCount++;
            return;
        }
        
        // Internal references
        refExtractor.addRefs(meta.intRefs, doc);
        
        // Collection references
        addCollectionRefs(meta, doc);
        
        // Extract fields by XPaths (if configured)
        xpathExtractor.extract(doc, meta.fields);
        
        // All fields as key-value pairs
        XmlNamespaces nsInfo = autogenExtractor.extract(file, meta.fields);
        
        // Search fields
        searchExtractor.extract(doc, meta.fields);
        
        // File information (name, size, checksum)
        fileDataExtractor.extract(file, meta, config.fileInfo.fileRef);
        
        // Save data
        save(meta, nsInfo);
    }

    
    private void addCollectionRefs(Metadata meta, Document doc) throws Exception
    {
        List<BundleMetadataExtractor.BundleMemberEntry> bmes = bundleExtractor.extractBundleMemberEntries(doc);

        for(BundleMetadataExtractor.BundleMemberEntry bme: bmes)
        {
            if(!bme.isPrimary && config.refsCfg.primaryOnly) continue;
            
            cacheRefs(bme);
            bundleExtractor.addRefs(meta.intRefs, bme);
        }
    }
    

    private void cacheRefs(BundleMetadataExtractor.BundleMemberEntry bme)
    {
        // Only cache primary references
        if(!bme.isPrimary) return;
        
        LidVidCache cache = RefsCache.getInstance().getCollectionRefsCache();

        if(bme.lidvid != null) cache.addLidVid(bme.lidvid);
        if(bme.lid != null) cache.addLid(bme.lid);
    }

}
