package gov.nasa.pds.harvest.crawler;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.nasa.pds.harvest.cfg.model.Configuration;
import gov.nasa.pds.harvest.meta.XPathExtractor;
import gov.nasa.pds.registry.common.meta.AutogenExtractor;
import gov.nasa.pds.registry.common.meta.BasicMetadataExtractor;
import gov.nasa.pds.registry.common.meta.FileMetadataExtractor;
import gov.nasa.pds.registry.common.meta.InternalReferenceExtractor;
import gov.nasa.pds.registry.common.meta.SearchMetadataExtractor;


/**
 * Base class to process PDS4 XML label files
 * @author karpenko
 */
public class BaseProcessor
{
    // Skip files bigger than 10MB
    protected static final long MAX_XML_FILE_LENGTH = 10_000_000;

    protected Logger log;
    
    protected Configuration config;
    protected Counter counter;
    
    protected DocumentBuilderFactory dbf;

    protected BasicMetadataExtractor basicExtractor;
    protected AutogenExtractor autogenExtractor;
    protected FileMetadataExtractor fileDataExtractor;
    protected InternalReferenceExtractor refExtractor;
    protected SearchMetadataExtractor searchExtractor;
    
    protected XPathExtractor xpathExtractor;


    /**
     * Constructor.
     * @param config Harvest configuration parameters
     * @param counter Counter of processed products
     * @throws Exception Generic exception
     */
    public BaseProcessor(Configuration config, Counter counter) throws Exception
    {
        this.config = config;
        this.counter = counter;

        log = LogManager.getLogger(this.getClass());
        
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);

        basicExtractor = new BasicMetadataExtractor();
        refExtractor = new InternalReferenceExtractor();
        searchExtractor = new SearchMetadataExtractor();
        xpathExtractor = new XPathExtractor();
        
        autogenExtractor = new AutogenExtractor();
        if(config.autogen != null)
        {
            autogenExtractor.setClassFilters(config.autogen.classFilterIncludes, config.autogen.classFilterExcludes);
            autogenExtractor.setDateFields(config.autogen.dateFields);
        }
        
        fileDataExtractor = new FileMetadataExtractor();
        if(config.fileInfo != null)
        {
            fileDataExtractor.setProcessDataFiles(config.fileInfo.processDataFiles);
            fileDataExtractor.setStoreLabels(config.fileInfo.storeLabels, config.fileInfo.storeJsonLabels);
        }
    }
}
