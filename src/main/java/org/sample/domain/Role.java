package org.sample.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Role {
    public enum Roles {
        USER("User"),
        ADMINISTRATOR("Administrator");

        private String label;

        Roles(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @JsonIgnore
    @GraphId
    private Long id;

    @Indexed(unique = true)
    private String uid;

    private String name;

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role(String uid) {
        setUid(uid);
    }

    public Role(String uid, String name) {
        setUid(uid);
        setName(name);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
