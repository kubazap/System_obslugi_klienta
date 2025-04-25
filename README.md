# System Obsługi Klienta

Projekt aplikacji webowej napisany w Java Spring Boot z wykorzystaniem PostgreSQL oraz Thymeleaf. Aplikacja umożliwia zarządzanie wizytami, klientami oraz pracownikami z zaimplementowanym mechanizmem dwuetapowej weryfikacji (TOTP).

## 🚀 Jak uruchomić projekt lokalnie?

Aby uruchomić aplikację lokalnie, wykonaj następujące kroki:

### 1. Uruchom serwer PostgreSQL

Upewnij się, że masz uruchomioną usługę **PostgreSQL**.

### 2. Stwórz bazę danych

Zaloguj się do lokalnej bazy danych PostgreSQL i utwórz nową bazę danych o nazwie:

```sql
CREATE DATABASE system_obslugi_klienta;
```

### 3. Ustaw wymagane zmienne środowiskowe

W środowisku uruchomieniowym (np. IntelliJ IDEA → **Edit Configurations → Environment variables**) ustaw poniższe zmienne:

| Zmienna środowiskowa    | Opis                                                |
|-------------------------|-----------------------------------------------------|
| `SPRING_MAIL_USERNAME`  | Login SMTP wygenerowany w Brevo (np. `user@smtp-brevo.com`) |
| `SPRING_MAIL_PASSWORD`  | Hasło SMTP wygenerowane w Brevo                     |
| `EMAIL_SENDER_EMAIL`    | Zweryfikowany adres nadawcy e-mail (np. `kontakt@twojadomena.pl`) |
| `db_username`           | Nazwa użytkownika PostgreSQL (np. `postgres`)       |
| `db_password`           | Hasło użytkownika PostgreSQL                        |

**Przykład konfiguracji w IntelliJ:**  
```
SPRING_MAIL_USERNAME=user@smtp-brevo.com;SPRING_MAIL_PASSWORD=twoje-haslo-smtp;EMAIL_SENDER_EMAIL=kontakt@twojadomena.pl;db_username=postgres;db_password=secret
```

### 4. Uruchom projekt

Uruchom projekt z poziomu IDE (np. IntelliJ IDEA) lub za pomocą Maven:

```bash
./mvnw spring-boot:run
```

Aplikacja będzie dostępna pod adresem:
```
http://localhost:8080
```

### 5. Import danych początkowych do bazy danych

Zaimportuj dane początkowe korzystając z pliku SQL, który znajduje się w projekcie:

```
src/main/resources/static/databases/all.sql
```

Możesz to zrobić np. za pomocą narzędzia `psql` w konsoli:

```bash
psql -U postgres -d system_obslugi_klienta -f sciezka_do_pliku/all.sql
```

## 📦 Technologie wykorzystane w projekcie

- **Spring Boot** – Framework aplikacji webowej
- **PostgreSQL** – Baza danych
- **Thymeleaf** – Silnik szablonów HTML
- **Spring Security** – Mechanizm uwierzytelniania (z TOTP)
- **Brevo (Sendinblue)** – Obsługa e-mail SMTP
- **Bootstrap 4** – Frontend
