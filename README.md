# API de gestion des Pals

![CI](https://github.com/LimHounTchenAime/api_gestion_pals/actions/workflows/ci.yml/badge.svg)

API REST en Spring Boot pour consulter et gérer les Pals de *Palworld* : fiche de chaque Pal, types, compétences,
classements par rareté ou par prix. Les 137 Pals du fichier `pals.json` sont chargés en base au démarrage.

## Stack

- Java 21, Spring Boot 3.5 (Web, Data JPA)
- Base H2 en mémoire
- Documentation OpenAPI / Swagger UI (springdoc)
- Tests JUnit 5 + Mockito + MockMvc

## Lancer le projet

```bash
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```

L'API écoute sur http://localhost:8080 :

- Swagger UI : http://localhost:8080/swagger-ui/index.html
- Console H2 : http://localhost:8080/h2-console (URL JDBC `jdbc:h2:mem:palsdb`, utilisateur `sa`, sans mot de passe)

## Endpoints

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/pals` | Liste de tous les Pals |
| GET | `/pals/{id}` | Un Pal par identifiant |
| GET | `/pals/by-name?name=Lamball` | Un Pal par nom |
| GET | `/pals/by-type?type=fire` | Pals d'un type |
| GET | `/pals/sorted/rarity?limit=10` | Les Pals les plus rares |
| GET | `/pals/sorted/price?limit=10` | Les Pals les plus chers |
| POST | `/pals` | Créer un Pal |
| PUT | `/pals/{id}` | Modifier un Pal |
| DELETE | `/pals/{id}` | Supprimer un Pal |
| GET / POST | `/pals/{id}/skills` | Compétences d'un Pal / en ajouter une |
| PUT | `/pals/{id}/skills/{skillId}` | Modifier une compétence |
| GET / POST | `/pals/{id}/types` | Types d'un Pal / en ajouter un |
| DELETE | `/pals/{id}/types/{typeId}` | Retirer un type |

Exemple :

```bash
curl "http://localhost:8080/pals/sorted/rarity?limit=3"
```

## Tests

```bash
./mvnw test
```

33 tests : contexte Spring, contrôleur (MockMvc) et service.

## Structure

```
src/main/java/com/example/api_gestion_pals
├── controller/   PalController (routes REST)
├── service/      PalService (logique métier)
├── DAO/          PalDAO (Spring Data JPA)
├── model/        entités JPA (Pal, compétences, types, aptitudes, stats)
└── config/       PalDataLoader (import de pals.json au démarrage)
```
