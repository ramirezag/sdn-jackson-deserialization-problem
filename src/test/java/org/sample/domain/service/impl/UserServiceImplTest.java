package org.sample.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sample.domain.Role;
import org.sample.domain.User;
import org.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class UserServiceImplTest {
    private Log log = LogFactory.getLog(getClass());
    @Autowired
    private UserService userService;

    @Autowired
    private Neo4jTemplate template;

    @Autowired
    private PlatformTransactionManager txnManager;

    @Before
    public void cleanup() {
        Neo4jHelper.cleanDb(template);

        TransactionTemplate txnTemplate = new TransactionTemplate(txnManager, new DefaultTransactionDefinition());
        txnTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Role uRole = new Role(Role.Roles.USER.name(), Role.Roles.USER.getLabel());
                template.save(uRole);
            }
        });
    }

    @Test
    public void createTest() throws Exception {
        User user1 = new User.Builder().setUsername("username1").setPassword("password1").setEmail("test1@gmail.com").build();
        User user2 = new User.Builder().setUsername("username2").setPassword("password2").setEmail("test2@gmail.com").build();
        user1 = userService.create(user1);
        user2 = userService.create(user2);
        assertNotNull(user1.getUid());
        assertNotNull(user2.getUid());

        User result1 = userService.findByUid(user1.getUid());
        assertNotNull(result1.getRoleRepository());
        print(result1);
    }

    @Test
    public void deserializationTest() throws Exception {
        User user = new User.Builder().setUid("uid-123").setUsername("test-username1").setPassword("test-password1").setEmail("test-email@gmail.com").build();
        print(user);
    }

    private void print(User user) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n\n-------------------------------------------------------------------------");
        builder.append("\nId     - " + user.getId());
        builder.append("\nUid    - " + user.getUid());
        builder.append("\nUser   - " + user.getUsername());
        builder.append("\nPass   - " + user.getPassword());
        builder.append("\nEmail  - " + user.getEmail());
        builder.append("\n");
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
        builder.append(jsonString);
        builder.append("\n");
        log.debug(builder.toString());

        User userObject = mapper.readValue(jsonString, User.class);
        assertNotNull(userObject.getUsername());
        assertNotNull(userObject.getPassword());
        assertNotNull(userObject.getEmail());
    }
}
