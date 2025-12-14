# ===========================
# Etapa 1: build con Maven y Java 21
# ===========================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el contenido del servicio al contenedor
COPY . .

# Compilamos el proyecto (sin ejecutar los tests)
RUN mvn clean package -DskipTests

# ===========================
# Etapa 2: runtime con JDK 21
# ===========================
FROM eclipse-temurin:21-jdk

# Creamos el directorio de trabajo
WORKDIR /app

# Copiamos el jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto (ajústalo si tu servicio usa otro)
EXPOSE 8091

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
