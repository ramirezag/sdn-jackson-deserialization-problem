package org.sample.neo4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomTransactionEventHandler implements TransactionEventHandler {
    private Log log = LogFactory.getLog(getClass());
    private GraphDatabaseService graphDatabaseService;
    private IndexManager indexManager;


    @Autowired
    public void setGraphDatabaseService(GraphDatabaseService service) {
        this.graphDatabaseService = service;
        indexManager = graphDatabaseService.index();
        this.graphDatabaseService.registerTransactionEventHandler(this);
    }

    @Override
    public Object beforeCommit(TransactionData data) throws Exception {
        log.debug("Entering before commit: ");
        final String uidKey = "uid";
        // Classes that extends auditTrace will have creationDate and
        // lastModifiedDate. If this lastModifiedDate set, that means the node
        // is already indexed.
        final String creationDateKey = "creationDate";
        final String lastModifiedDateKey = "lastModifiedDate";
        for (Node node : data.createdNodes()) {
            /**
             *  We need to determine if the node is a class object.
             *  If so we need to make sure it has a uid property. If
             *  it does not, we will add it. If it does have we will
             *  ensure it has a value. If it already has a value then
             *  OK.
             */
            boolean hasUid = false;
            boolean isClass = false;
            boolean hasCreationDateKey = false;
            boolean hasLastModifiedDateKey = false;
            for (String key : node.getPropertyKeys()) {
                if (key.equalsIgnoreCase(uidKey) && !hasUid) {
                    hasUid = true;
                }
                if (key.equalsIgnoreCase("__type__") && !isClass) {
                    isClass = true;
                }
                if (key.equalsIgnoreCase(creationDateKey) && !hasCreationDateKey) {
                    hasCreationDateKey = true;
                }
                if (key.equalsIgnoreCase(lastModifiedDateKey) && !hasLastModifiedDateKey) {
                    hasLastModifiedDateKey = true;
                }
            }
            if (isClass && !hasUid) {
                node.setProperty(uidKey, "");
            } else if (!isClass) { // Not a class object so do nothing
                return null;
            }
            String uuid = (String) node.getProperty(uidKey);
            boolean updateIndex = false;
            if (uuid == null || uuid.length() == 0) {
                // Generate identifier
                uuid = UUID.randomUUID().toString();
                node.setProperty(uidKey, uuid);
                updateIndex = true;
            } else if (hasCreationDateKey) {
                if (!hasLastModifiedDateKey) {
                    updateIndex = true;
                } else {
                    Object value = node.getProperty(lastModifiedDateKey);
                    updateIndex = value == null ? true : ((Long) value) < 1;
                }
            }

            if (updateIndex) {
                // SDN will have an existing index based on class name, we will extract from node properties
                final String targetClass = node.getProperty("__type__").toString();
                final String[] parts = targetClass.split("\\.");
                if (parts.length > 0) { // Update index manually so @Indexed works on uid field
                    final String targetIndex = parts[parts.length - 1];
                    final Index<Node> index = indexManager.forNodes(targetIndex);
                    index.add(node, uidKey, uuid);
                } else {
                    System.err.println("Could not parse class type: " + targetClass);
                }
            }
        }
        return null;
    }

    @Override
    public void afterCommit(TransactionData data, Object state) {

    }

    @Override
    public void afterRollback(TransactionData data, Object state) {

    }
}
