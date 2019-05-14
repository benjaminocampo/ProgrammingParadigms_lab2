#**Models**
The models are created in order to represent the different entities in our
aplication. We have to represent the consumers and providers, items,
locations and orders.
In order to do that each of these have its own class **Consumer**, **Provider**, **Item**,
**Location**, **Order**.
They have a method *toMap* to show their information. We will use this to show it
in a response when and enpoint requires such information. 

**Consumer** and **Provider** inherit from something, **User**,  since 
both are users. We have chosen for user to be a trait because we wanted
to share interfaces and fields between the classes Consumer and Provider.
The trait User is:

    trait User {
        val username: String
        val locationId: Int
        private var balance: Float = 0

        def increment(amount: Float): Unit = balance += amount
        def decrement(amount: Float): Unit = balance -= amount
    }

We have a balance to represent the wallet of each of the users that will increase or decrease
according to the transaction created.
For this we have a method, *increment* (respectively *decrement*) to increment (respectively decrement)
such wallet. *increment* and *decrement* are the interface for the outside world.

Similarly to the classes **Provider** and **Consumer** we also have another classes, **Order**
and **Item** to represent orders and items respectively.

--------------------------------------------------------------------------------------------------------------------------------


Class **Order** takes the following parameters:

***providerUsername***: The provider's username associated with the order.
***consumerUsername***: The consumer's username associated with the order.
***item***: A list of items where each item is given by a tuple of its name and amount. 

The class **Order** has the following attributes:  

***consumerId***: The consumer's id associated with the order.  
***providerId***: The provider's id associated with the order.  
***status***: The order's status (payed, delivered, finished)  
***items***: A list of items that belongs to the consumer's order.  
***totalPrice***: The total price of the order that belongs to the consumer.  

The purpose that we have *providerId* and *consumerId* even though we already have
*providerUsername* and *consumerUsername* is that we also want to show (with *toMap* method) 
their ids and the information associated. To the same purpose we have the method 
*toMapDetail* to show a list of items with their ids, names, descriptions, price and amount.  

**Methods:**  
   
    toMap:  
        "id" -> id,  
        "consumerId" -> consumerId,  
        "consumerUsername" -> consumerUsername,  
        "consumerLocation" ->  Consumer.getValueById(consumerId, "locationId"),    
        "providerId" -> providerId,  
        "providerStoreName" -> Provider.getValueById(providerId, "storeName"),   
        "orderTotal" -> precioTotal,  
        "status" -> status  
        
                
        
    toMapDetail: 
        "id" -> id, 
        "name" -> name, 
        "description" -> desc, 
        "price" -> price, 
        "amount" -> cant
        
    changeStatus: Change the status of an order. 
    getStatus: Give the current order status.
    calcTotalPrice: Calculate the total price of an order.

*getValueById* takes the parameters *id* and *attr* and returns the value associated
with that information acording to the database's data.

*itemId*: It takes the parameters *itemName* an *providerName* to get the item's id according to data.

--------------------------------------------------------------------------------------------------------------------------------

Class **Item** takes the following parameters:  

***name***: The item's name.  
***description***: The item's description.  
***price***: The item's price.  
***providerId***: The id of the provider that sells the item.  
    
**Methods:**

    toMap:
        "id" -> id
        "name" -> name, 
        "price" -> price, 
        "description" -> description, 
        "providerId" -> providerId

--------------------------------------------------------------------------------------------------------------------------------


#**Endpoints**

All the endpoints are handled by a method that returns a Jsonresponse according 
to the action of the handler.

**@get("/api/consumers/")**  
*def consumers()*

These endpoints are handled like this:

We use the method Consumer.all to fetch a list of values of the Consumer's instances.
Then, since we want to show the information of each of these objects. What we are going to do is to create a new list which will contain, for each element of the list of values, its information. The method toMap shows the information of an object, so we are going to apply it to each element of the list of values, and so, obtaining the information of each one. The created list that contains each element's information, is what is returned as the response.

**@postJson("api/consumers")**:  
*consumer(username, locationName)*

This method creates a new consumer with the given *username* and *locationName*.
In order to create a new consumer we have to make sure that its name given by
*username* is not already, whether in the consumers or providers. 
We also have to make sure that *locationName* corresponds to an existing location.
If it is not the case we return an error message.

After do that we can make an instance of the class **Consumer**
to create it and save it into the database.

Finally the result of the method is the consumer's id.

**@get("/api/providers")**:  
*providers(locationName = "")*

This method fetches a list of the providers that are in a location given by *locationName*. If no argument is given by parameter, then *locationName* will be an empty string, in this case we are going to show all the providers in all the locations. Otherwise, if a *locationName* is set, we are going to show the information about the providers in that location given by *locationName*.
In order to fetch a list of the providers we have to make sure that *locationName* corresponds to an existing location.
If it is not the case (i.e, a *locationName* was given but corresponds to no existing location), we return an error message. Otherwise we return the list.

**@postJson("/api/providers")**:  
*providers(username, storeName, locationName, maxDeliveryDistance)*

This method creates a new provider and saves it into the database.

In order to do that we have to make sure the following asserts:
- *maxDeliveryDistance* is to be positive. 
- *locationName* not to correspond to an existing location.
- *username* is not already, whether in the consumers or providers.
- *storeName* is not already.
If it is not the case we return an error message. Otherwise 
we make an instance of the class Provider to create it and then save it.
Finally return the provider's id.

**@ post("/api/users/delete/:username")**:  
*delete(username)*

Deletes an existing user from the users (consumers or providers).
In order to do that we have to make sure that *username* corresponds to an existing user (consumer or provider respectively). Since there are not a consumer and a provider with the same username, so if the *username* matches with a consumer's name and we delete it, we are going to be sure that it is deleted and the same if it matches with a provider's name.
So that is exactly what we do, first we ask if it is a consumer and, if so, we delete it. If it is not the case, we do the same with the providers.

If it neither matches a consumer nor provider, we return an error message.

**@get("/api/items/")**:  
*items(providerUsername = "")*

This method fetches a list of items of an existing provider. If no argument is given by parameter, then *providerUsername* will be an empty string, in this case we are going to show all the items of all providers. Otherwise, if a *providerUsername* is set, we are going to show the information about the items of the provider given by *providerUsername*.
In order to fetch a list of items we have to make sure that *providerUsername* corresponds to an existing provider.
If it is not the case (i.e, a *providerUsername* was given but corresponds to no existing provider), we return an error message. Otherwise we return the list.

**@postJson("/api/items")**:  
*items(name, description, price, providerUsername)*:  

This method creates and item and saves it into the database.
In order to do that we have to make sure the following asserts:
- *price* is to be positive.
- For *providerUsername* not to correspond to an existing provider.

After do that we proceed to filter by the provider's id, all the items.
To get the provider's id we do the same that we mention above.
Then we have to make sure that the *name* does not already exist in the filtered list. If so, we return an error.

If it is not the case, we proceed to create an instance of the class Item to create it and then save it.
Finally we return the item's id.

**@ post("/api/items/delete/:id")**  
*itemsDelete(id)*:  

This method deletes an item according to the id given by parameter.
Before deleting the item we have to make sure that the item already exists. For this we use find (#). 
Therefore, if *find(id)* gives some(the_item) then we know it exists and we proceed to delete the_item. If *find(id)* gives None then we know that the item does not exist and we return an error message.

(#) *find(itemId)* returns Some(item) when the item whose id is *itemId* and returns None when the item is not found.


**@get("/api/orders")**:  
*def orders(username: String)*:  

This method fetches all the orders related to a username given by parameter.
Since a username can be a provider's username or a consumer's username we choose not to make asumptions and we simply handle both cases.
First we make sure that the the username corresponds to a provider's username or a consumer's username. If so, we filter all the orders according to the previous step and we return it. If not so, we return an error message.

**@get("/api/orders/detail/:id")**:  
*detail(id)*:  

This method fetches the detail of the item according to the id given to it by parameter.
Before returning the order's details we have to make sure that the order already exists. For this we use find (#). 
Therefore, if *find(id)* gives some(the_order) then we know it exists and we proceed to return the_order's details. If *find(id)* gives None then we know that the order does not exist and we return an error message.

(#) *find(orderId)* returns Some(order) when the order whose id is *orderId* and returns None when the order is not found.

**@postJson("/api/orders")**:  
*def createOrder(providerUsername: String, consumerUsername: String, items: List[ItemsToOrder]):*  

This method creates a new order associated to a consumer and a provider. It receives by parameters a providerUsername, a consumerUsername and a list of items.
In this case the list of items comes in a Json format. Then we have to deserealize it. Deserialization is handled using the uPickle JSON library.
First we create a case class **ItemsToOrder** that will match the format in which we want those items.
Then the items of the list given by parameter will match with the format stablished in the class ItemsToOrder. 

Recall that items is a list of objects (where each object has a name and an amount) but we need a list of tuples (name, amount) where each tuple corresponds to an object of the list.
To do that we map *items* to a new list, *itemsToOrder* that contains said tuples.

Now we have to make sure of the following things:
- The amount of each order is to be positive.
- For provider and consumer (that are associated to *providerUsername* and *consumerUsername*, respectively) to already exist.
- For each item in the list *itemsToOrder* to exist.
If not so, we return an error.
If so, we can make an instance of a new order to create it and then save it into the database.

Since some of the consumer's money has to go to the provider's money, then we are to balance these two. We do so by decreasing the consumer's money by *totalPrice* and increasing the provider's money by the same *totalPrice*.

To do that, we use a method that it will be our interface to increment (respectively decrement) the provider's money (respectively the consumer's money). The purpose of these methods is to not directly modifiy the balance of each one. This is the encapsulation.

Finally we return the order's id.


**@ post("/api/orders/delete/:id")**:  
*def deleteOrder(id)*  

This method deletes an order according to the id given to it by parameter.
Before deleting the order we have to make sure that the order already exists. For this we use find (#). 
Therefore, if *find(id)* gives some(the_order) then we know it exists and we proceed to delete the_order. If *find(id)* gives None then we know that the order does not exist and we return an error message.

(#) *find(orderId)* returns Some(order) when the order, whose id is *orderId*, is found and returns None when the order is not found.

**@ post("/api/orders/deliver/:id")**:  
*def deliverOrder(id)*  

This method delivers an order according to the id given to it by parameter.
Before delivering the order we have to make sure that the order already exists. For this we use find (#). 
Therefore, if *find(id)* gives some(the_order) then we know it exists and we proceed to change its status to *delivered*. We are only going to change its status to *delivered* if its current status is *payed*. If not so, then we just return an error. To change its status we call its method *changeStatus("delivered")*.
If *find(id)* gives None then we know that the order does not exist and we return an error message.

(#) *find(orderId)* returns Some(order) when the order, whose id is *orderId*, is found and returns None when the order is not found.