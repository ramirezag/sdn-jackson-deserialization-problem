package org.sample.service.impl;

import org.sample.domain.User;
import org.sample.repository.UserRepository;
import org.sample.service.RoleService;
import org.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Neo4jTemplate template;

    @Autowired
    private PlatformTransactionManager txnManager;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    public User create(final User user) {
        final User existingUser = userRepository.findByPropertyValue("username", user.getUsername());

        if (existingUser != null)
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists.");

        // Validate roles
        roleService.inflateRoles(user.getRoles());
        user.addDefaultRoles();

        TransactionTemplate txnTemplate = new TransactionTemplate(txnManager, new DefaultTransactionDefinition());
        txnTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                template.save(user);
            }
        });
        // java.lang.NullPointerException at org.springframework.data.neo4j.fieldaccess.PropertyFieldAccessorFactory$PropertyFieldAccessor.doGetValue(PropertyFieldAccessorFactory.java:85)
        //return template.fetch(user);
        return template.findOne(user.getId(), User.class);
    }

    public User findByUid(String uid) {
        return userRepository.findByUid(uid);
    }
}
