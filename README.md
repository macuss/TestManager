# 🧪 TestManager

Este proyecto es un framework de gestión de pruebas desarrollado con **Java**, los features exportados usarán **Selenium WebDriver** para la interacción con el navegador y **Cucumber** con **Gherkin** para la gestión de escenarios de prueba bajo la metodología BDD (Behavior-Driven Development).

## 🚀 Tecnologías Utilizadas para la gestión de Test

* **Lenguaje:** Java 17+
* **Automatización:** Selenium WebDriver
* **BDD:** Cucumber & Gherkin
* **Base de Datos:** MySQL (con patrón DAO)
* **IDE:** Eclipse / IntelliJ
* **Gestión de Versiones:** Git & GitHub

## 📂 Estructura del Proyecto

* `src/model`: Clases de entidad que representan los objetos de negocio.
* `src/dao`: Capa de persistencia para interactuar con la base de datos MySQL.
* `src/util`: Clases de utilidad, incluyendo la conexión a la base de datos (`DBConnection`).
* `src/features`: Escenarios de prueba escritos en lenguaje natural (Gherkin).
* `src/stepDefinitions`: Implementación en Java de los pasos definidos en los archivos feature.
* `db/`: Script SQL para replicar la base de datos localmente.

## 🛠️ Configuración Local

Para ejecutar este proyecto en tu máquina, seguí estos pasos:

### 1. Base de Datos
1. Importa el archivo `.sql` ubicado en la carpeta `/db` en tu **MySQL Workbench**.
2. Asegúrate de que el servicio de MySQL esté corriendo.

### 2. Configuración de Credenciales
Este proyecto utiliza un archivo de propiedades para no exponer datos sensibles. 
1. Crea un archivo llamado `config.properties` en la carpeta `src/`.
2. Agrega el siguiente contenido con tus credenciales locales:
   ```properties
   db.url=jdbc:mysql://localhost:3306/testmanager
   db.user=tu_usuario
   db.password=tu_contraseña
