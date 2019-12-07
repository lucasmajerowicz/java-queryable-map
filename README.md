#Queryable Map 
========

### Accompanying blog post [here](http://hecodes.com/2019/12/implementing-a-queryable-map-in-java/)

## Description

This is a basic implementation of a Java Collection that allows for efficient retrieval of data by multiple attributes present in the data.
Example: take the following User entity Class

```java
public class User {
        private String id;
        private String name;
        private List<String> phones;
        private String country;
    
    // getters
}
```

Say we want to store a collection of Users and be able to retrieve them efficiently either by id, name or by one of their phone numbers:

```java
QueryableMap.Builder<String, User> builder = QueryableMap.newBuilder();
QueryableMap<String, User> map = builder
        .keyFunction(User::getId)
        .addIndex("name", User::getName)
        .addIndex("phone", User::getPhones)
        .build();

// insert users
map.put(new User(...));
map.put(new User(...));
map.put(new User(...));

// fetch user by id
User user1 = map.get("1");

// fetch users by name
Collection<User> usersCalledJohn = map.query("name", "John");

// fetch users by name
Collection<User> usersWithPhone1234 = map.query("phone", "1234");
```