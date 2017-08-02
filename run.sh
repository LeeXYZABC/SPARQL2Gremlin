
TEMP=`ls ./target/lib/*.jar`
MAVEN_JARS=`echo $TEMP | sed 's/ /:/g'`

time java -server -cp .:target/SPARQL2Gremlin-1.0-SNAPSHOT.jar:$MAVEN_JARS com.liang.translator.SPARQL2Gremlin.SparqlToGremlinTest

