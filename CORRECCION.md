
    "parser": "babel-eslint",
 04		
## Corrección		
	Tag o commit corregido:	lab-2
		
### Entrega y git		72,00%
	Informe	30,00%
	Commits de cada integrante	100,00%
	En tiempo y con tag correcto	100,00%
	Commits frecuentes y con nombres significativos	100,00%
### Funcionalidad		100,00%
	Se puede compilar y correr el servidor	100,00%
	Se pueden crear y recuperar usuarios	100,00%
	Se pueden crear y recuperar items	100,00%
	Se pueden crear y recuperar orders	100,00%
	Guardan los objetos luego de crearlos y actualizarlos	100,00%
### Modularización y diseño		92,50%
	Respetaron la estructura original del código	100,00%
	En los controladores sólo hay código de validación de errores, i.e. no hay lógica de los objetos	100,00%
	La mayor parte de la funcionalidad está en la clase y no en el object companion	100,00%
	Uso de métodos heredados con super, por ejemplo en toMap	100,00%
	Los usuarios actualizan su propio balance	100,00%
	Las órdenes actualizan su propio estado	50,00%
### Calidad de código		100,00%
	Buenas prácticas funcionales	100,00%
	Líneas de más de 120 caracteres	100,00%
	Estilo de código	100,00%
	Estructuras de código simples	100,00%
### Opcionales		
	Puntos estrella	100,00%
		
# Nota Final		10
		
		
# Comentarios		
	- El informe está super completo, excepto que no es lo que pedíamos. No era necesario describir la API, sino que tenían que responder dónde y cómo usaban herencia, polimorfismo, traits y sobrecarga de operadores.	
	- Las órdenes no sólo deben tener un método para actualizar el estado, sino que la implementación del estado (en este caso a través de un string) también debería ser transparente. Conviene usar métodos de Order como pay() o deliver() que conozcan la implementación interna, para abstraer al controlador de estos detalles	
	- Se dejaron un archivo cmd.sh	
	- La función itemId en el object companion de Order debería ir en el object companion de Item. Podría ir en el Modelo Order si no pasaran el provider como parámetro, sino directamente el atributo del objeto.	
