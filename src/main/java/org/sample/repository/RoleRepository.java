package org.sample.repository;

import org.sample.domain.Role;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends GraphRepository<Role> {
    @Query("START role=node:Role( uid = {0} ) RETURN role")
    Role findByUid(String uid);

    @Query("START n=node:User( uid = {0} ) MATCH n-[:HAS_ROLE]->role RETURN role")
    EndResult<Role> findByUser(String uid);
}
