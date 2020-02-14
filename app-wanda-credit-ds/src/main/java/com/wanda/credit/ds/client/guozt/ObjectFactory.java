
package com.wanda.credit.ds.client.guozt;

import javax.xml.bind.annotation.XmlRegistry;
    

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the cn.id5.gboss.businesses.validator.service.app package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.id5.gboss.businesses.validator.service.app
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link QuerySingleResponse }
     * 
     */
    public QuerySingleResponse createQuerySingleResponse() {
        return new QuerySingleResponse();
    }

    /**
     * Create an instance of {@link QuerySingle }
     * 
     */
    public QuerySingle createQuerySingle() {
        return new QuerySingle();
    }

    /**
     * Create an instance of {@link QueryBatch }
     * 
     */
    public QueryBatch createQueryBatch() {
        return new QueryBatch();
    }

    /**
     * Create an instance of {@link QueryBatchResponse }
     * 
     */
    public QueryBatchResponse createQueryBatchResponse() {
        return new QueryBatchResponse();
    }

}
