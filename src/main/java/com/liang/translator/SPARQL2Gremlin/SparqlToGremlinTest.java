package com.liang.translator.SPARQL2Gremlin;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;

/**
 * Created by cnic-liliang on 2017/7/21.
 */
public class SparqlToGremlinTest {
    static String sparqlQuery1 =
            "select (count(distinct ?enzymeid) as ?num1) (count(distinct ?geneid) as ?num2) from <http://data.wdcm.org/smalldataCorrect> where {" +
                    "{?taxonid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/ancestorTaxid> <http://gcm.wdcm.org/data/gcmAnnotation1/taxonomy/1270>. " +
                    "?geneid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-taxon> " +
                    "?taxonid;" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-enzyme> ?enzymeid.}" +
                    "union{?geneid" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-taxon> <http://gcm.wdcm.org/data/gcmAnnotation1/taxonomy/1270>;" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-enzyme> ?enzymeid.}}";
    static String sparqlQuery2 =
            "select distinct ?taxonid ?enzymeid ?enzymeclass ?sysname ?enzymename from <http://data.wdcm.org/smalldataCorrect> where {" +
                    "{?taxonid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/ancestorTaxid> <http://gcm.wdcm.org/data/gcmAnnotation1/taxonomy/1270>. " +
                    "?geneid a <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/GeneNode>;" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-taxon> ?taxonid;" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-enzyme> ?enzymeid." +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/name> ?enzymename.}" +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/class> ?enzymeclass.}" +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/sysname> ?sysname.}}" +
                    "union{?geneid a <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/GeneNode>;<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-taxon> <http://gcm.wdcm.org/data/gcmAnnotation1/taxonomy/1270>;" +
                    "<http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-enzyme> ?enzymeid." +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/name> ?enzymename.}" +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/class> ?enzymeclass.}" +
                    "optional{?enzymeid <http://gcm.wdcm.org/ontology/gcmAnnotation/v1/sysname> ?sysname.}}} " +
                    "order by ?enzymeid OFFSET 0 Limit 15";

    static public void run(){
        System.out.println("SPARQL Query1: " + sparqlQuery1);
        Query query1 = QueryFactory.create(sparqlQuery1, Syntax.syntaxSPARQL);
        System.out.println("\nGremlin Query1: " + new SPARQL2Gremlin().Transform(query1));
        System.out.println("\nSPARQL Query2: " + sparqlQuery2);
        Query query2 = QueryFactory.create(sparqlQuery2, Syntax.syntaxSPARQL);
        System.out.println("\nGremlin Query2: " + new SPARQL2Gremlin().Transform(query2));
    }

    static public void main(String[] args) {
        run();
    }
}
