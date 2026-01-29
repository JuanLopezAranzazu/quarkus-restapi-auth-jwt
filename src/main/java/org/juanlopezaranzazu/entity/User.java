package org.juanlopezaranzazu.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {
    
    public ObjectId id;
    public String username;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public List<String> roles;
    public String refreshToken;
    public LocalDateTime refreshTokenExpiry;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

}
