#!/bin/bash

echo "Creando Locations"
http POST http://localhost:4000/api/locations name="Nueva Cordoba" coordX:=0 coordY:=0
http POST http://localhost:4000/api/locations name="Cordoba" coordX:=0 coordY:=0
http POST http://localhost:4000/api/locations name="Ciudad Universitaria" coordX:=0 coordY:=0
http POST http://localhost:4000/api/locations name="Alberdi" coordX:=0 coordY:=0

echo "Creando Consumers"
http POST http://localhost:4000/api/consumers username="Ben1" locationName="Nueva Cordoba"
http POST http://localhost:4000/api/consumers username="Ben2" locationName="Cordoba"
http POST http://localhost:4000/api/consumers username="Ben3" locationName="Ciudad Universitaria"
http POST http://localhost:4000/api/consumers username="Ben4" locationName="Alberdi"

echo "Creando Providers"
http POST http://localhost:4000/api/providers username="McDonalds" storeName="McDonalds" locationName="Nueva Cordoba" maxDeliveryDistance:=100
http POST http://localhost:4000/api/providers username="PizzaHart" storeName="PizzaHart" locationName="Ciudad Universitaria" maxDeliveryDistance:=100
http POST http://localhost:4000/api/providers username="ClubDeLaMilanesa" storeName="ClubDeLaMilanesa" locationName="Alberdi" maxDeliveryDistance:=100
http POST http://localhost:4000/api/providers username="BurguerKing" storeName="BurguerKing" locationName="Nueva Cordoba" maxDeliveryDistance:=100

echo "Testeando Provider que ya existe"
http POST http://localhost:4000/api/providers username="McDonalds" storeName="McDonalds" locationName="Nueva Cordoba" maxDeliveryDistance:=100


echo "Consultas"
http GET http://localhost:4000/api/locations
http GET http://localhost:4000/api/providers/
http GET http://localhost:4000/api/providers?locationName="Nueva Cordoba"
http GET http://localhost:4000/api/providers?locationName="Ciudad Universitaria"
http GET http://localhost:4000/api/providers?locationName=Alberdi



echo "Agregando Items"
http POST http://localhost:4000/api/items/ name="Empanadas" description="Salteñas" price:=299.99 providerUsername="McDonalds"
http POST http://localhost:4000/api/items/ name="Ñoquis con Salsa" description="Muy ricos" price:=199.99 providerUsername="BurguerKing"
http POST http://localhost:4000/api/items/ name="Pizza Napolitana" description="8 Porciones" price:=299.99 providerUsername="PizzaHart"
http POST http://localhost:4000/api/items/ name="Ensalada de Frutas" description="muchas frutas" price:=299.99 providerUsername="McDonalds"

echo "Poniendo Items que ya existen"
http POST http://localhost:4000/api/items/ name="Empanadas" description="Salteñas" price:=299.99 providerUsername="McDonalds"
http POST http://localhost:4000/api/items/ name="Empanadas" description="Salteñas" price:=299.99 providerUsername="McDonalds"

echo "Items con precios negativos"

http POST http://localhost:4000/api/items/ name="Arroz con leche" description="Salteñas" price:=-10.99 providerUsername="McDonalds"
http POST http://localhost:4000/api/items/ name="Mila vegana" description="Salteñas" price:=-200.99 providerUsername="McDonalds"

echo "Agregando Items a provider que no existen"

http POST http://localhost:4000/api/items/ name="Ensalada de fruta" description="muchas frutas" price:=299.99 providerUsername="KFC"
http POST http://localhost:4000/api/items/ name="fideos" description="Salteñas" price:=299.99 providerUsername="KFC2"

echo "Consultas Items"

http GET http://localhost:4000/api/items/

echo "McDonalds"
http GET http://localhost:4000/api/items?providerUsername=McDonalds

echo "BurguerKing"
http GET http://localhost:4000/api/items?providerUsername=BurguerKing

echo "PizzaHart"
http GET http://localhost:4000/api/items?providerUsername=PizzaHart


echo "Pidiendo Ordenes"
http POST http://localhost:4000/api/orders/ providerUsername="McDonalds" consumerUsername="Ben1" items:='[{"name": "Empanadas", "amount": 100}, {"name": "Ensalada de Frutas", "amount": 10}]'
http POST http://localhost:4000/api/orders/ providerUsername="PizzaHart" consumerUsername="Ben2" items:='[{"name": "Pizza Napolitana", "amount": 10}]'
http POST http://localhost:4000/api/orders/ providerUsername="BurguerKing" consumerUsername="Ben3" items:='[{"name": "Ñoquis con Salsa", "amount": 100}]'
http POST http://localhost:4000/api/orders/ providerUsername="McDonalds" consumerUsername="Ben1" items:='[{"name": "Empanadas", "amount": 100}, {"name": "Ensalada de Frutas", "amount": 10}]'

echo "Pidiendo Ordenes negativo!"
http POST http://localhost:4000/api/orders/ providerUsername="BurguerKing" consumerUsername="Ben3" items:='[{"name": "Ñoquis con Salsa", "amount": -100}]'

echo "Pidiendo Orden no hay consumidor"
http POST http://localhost:4000/api/orders/ providerUsername="BurguerKing" consumerUsername="Gabi" items:='[{"name": "Ñoquis con Salsa", "amount": 100}]'

echo "Pidiendo Orden no hay proveedor"
http POST http://localhost:4000/api/orders/ providerUsername="KFC" consumerUsername="Ben" items:='[{"name": "Ñoquis con Salsa", "amount": 100}]'



echo "Consultando Ordenes"
http GET http://localhost:4000/api/orders/ username=="Ben1"
http GET http://localhost:4000/api/orders/ username=="Ben2"
http GET http://localhost:4000/api/orders/ username=="Ben3"

echo "Consultando Ordenes Detalle"
http GET http://localhost:4000/api/orders/detail/1
http GET http://localhost:4000/api/orders/detail/2
http GET http://localhost:4000/api/orders/detail/3

http GET http://localhost:4000/api/items/
echo "Cambiando estado a delivered"
http POST http://localhost:4000/api/orders/deliver/1
http POST http://localhost:4000/api/orders/deliver/2
http POST http://localhost:4000/api/orders/deliver/3
http POST http://localhost:4000/api/orders/deliver/4

echo "Analizando si hubo cambio de estado"
http GET http://localhost:4000/api/orders/ username=="Ben1"
http GET http://localhost:4000/api/orders/ username=="Ben2"
http GET http://localhost:4000/api/orders/ username=="Ben3"

echo "Agregando comentario a una orden"                                         
http POST http://localhost:4000/api/orders/comment/ orderId:=1 comment="Llego aplastadas las empanadas, estoy furiosisimo" punctuation:=1
http POST http://localhost:4000/api/orders/comment/ orderId:=2 comment="Llego aplastadas las empanadas, todo mal" punctuation:=5
http POST http://localhost:4000/api/orders/comment/ orderId:=4 comment="Banana podrida 0 puntos" punctuation:=5
                                                                                 
echo "Viendo perfiles de los providers"                                         
http GET http://localhost:4000/api/provider/perfil/McDonalds                    
http GET http://localhost:4000/api/provider/perfil/PizzaHart   

echo "Eliminando Items"
http POST http://localhost:4000/api/items/delete/1
http POST http://localhost:4000/api/items/delete/2
http GET http://localhost:4000/api/items/

echo "Mostrando Providers antes de haber eliminado usuario"
http GET http://localhost:4000/api/providers/

echo "Eliminando Usuarios"
http POST http://localhost:4000/api/users/delete/McDonalds
http POST http://localhost:4000/api/users/delete/PizzaHart

echo "Eliminando El mismo usuario"
http POST http://localhost:4000/api/users/delete/McDonalds

echo "Mostrando Prviders luego de haber eliminado usuario"
http GET http://localhost:4000/api/providers/

echo "Eliminando Ordenes"
http POST http://localhost:4000/api/orders/delete/1
http POST http://localhost:4000/api/orders/delete/2
http POST http://localhost:4000/api/orders/delete/3

echo "Mostrando Balance Consumers"
http GET http://localhost:4000/api/consumers
