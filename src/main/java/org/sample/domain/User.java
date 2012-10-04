package org.sample.domain;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.graphdb.Direction;
import org.sample.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@Configurable
@NodeEntity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@JsonIgnoreProperties(value = {"persistentState","entityState","template","nodeId"})
public class User {
    public static class Builder {
        private String uid = "";
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String password;
        private Locale locale = Locale.US;
        private Set<Role> roles = new HashSet<Role>();

        public Builder() {
        }

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setFirstName(String fName) {
            this.firstName = fName;
            return this;
        }

        public Builder setLastName(String lName) {
            this.lastName = lName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder addRole(Role role) {
            roles.add(role);
            return this;
        }

        public User build() {
            final User user = new User();
            user.setUid(this.uid);
            user.setFirstName(this.firstName);
            user.setLastName(this.lastName);
            user.setUsername(this.username);
            user.setEmail(this.email);
            user.setPassword(this.password);
            user.setLocale(this.locale);
            user.setRoles(roles);
            return user;
        }
    }

    @JsonIgnore
    @Autowired
    private RoleRepository roleRepository;

    @JsonIgnore
    @GraphId
    private Long id;

    @Fetch
    @RelatedTo(type = "HAS_ROLE", direction = Direction.OUTGOING)
    private Set<Role> roles = new HashSet<Role>();

    private String uid;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String username;

    @Indexed
    private String email;

    private String password;

    // Default to US
    private Locale locale = Locale.US;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public User addDefaultRoles() {
        addRole(roleRepository.findByUid(Role.Roles.USER.toString()));
        return this;
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }
}
