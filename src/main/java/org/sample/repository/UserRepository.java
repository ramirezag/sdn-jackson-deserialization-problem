package org.sample.repository;

import org.sample.domain.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GraphRepository<User>
{
    @Query( "START user=node:User( uid = {0} ) RETURN user" )
    User findByUid( String uid );

    @Query( "START role=node:Role( uid = {0} ) MATCH role<-[:HAS_ROLE]-users RETURN users" )
    EndResult<User> findByRole( String uid );
}