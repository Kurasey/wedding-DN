
# WEMS - Wedding Event Management System

<p align="center">
  <img src="https://raw.githubusercontent.com/kaurami/my-wedding-assets/main/other/wems-logo.png" alt="WEMS Logo" width="200"/>
</p>

<p align="center">
  <strong>A modern, self-hostable, and highly configurable platform for creating elegant and interactive wedding e-invitations.</strong>
</p>

<p align="center">
    <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License: Apache 2.0">
    <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4.5-brightgreen.svg" alt="Spring Boot 3.4.5">
    <img src="https://img.shields.io/badge/container-Docker-blue.svg" alt="Container: Docker">
    <img src="https://img.shields.io/badge/status-active-brightgreen" alt="Project Status">
</p>

---

**WEMS** is a full-featured web application designed to create a beautiful, personalized wedding website. It provides a seamless experience for guests and a powerful administrative dashboard for the event organizers to manage all details, track RSVPs, and receive real-time notifications.

## ✨ Key Features

### 💌 For Guests
*   **Personalized Invitation Page**: Each family or guest group receives a unique, private link.
*   **Interactive RSVP Form**: A user-friendly form to confirm attendance, specify the number of guests, and list drink preferences.
*   **Elegant, Responsive Design**: A clean and beautiful interface that looks great on any device.
*   **Live Countdown Timer**: A real-time timer counting down to the special day.
*   **"Add to Calendar"**: Guests can download an `.ics` file to easily add the event to their personal calendar.
*   **Interactive Map**: An embedded Yandex Map showing the venue location for easy navigation.
*   **Dynamic QR Code**: A generated QR code that links directly to a Telegram group for guests.
*   **Atmospheric Music Player**: Background music to set the mood, with an easy-to-use toggle button.

### ⚙️ For Administrators
*   **Secure Admin Panel**: A password-protected dashboard to manage the entire event, featuring brute-force protection via rate limiting.
*   **Full Guest & Family Management**: Easily perform CRUD operations on families and individual guests.
*   **Comprehensive Dashboard**: At-a-glance statistics on confirmed guests, transfer/accommodation needs, and beverage preferences.
*   **Dynamic Timeline Management**: Create and manage the event's schedule with custom icons and drag-and-drop reordering.
*   **Visit History**: Track who has viewed their invitation page and when.
*   **Real-time Telegram Notifications**: Receive instant updates for new RSVP submissions, changes in a guest's attendance status, and more.
*   **Flexible Configuration**: All event details (names, dates, text, colors) are managed through a simple `.env` file.

## 🛠️ Technology Stack

| Component         | Technology / Library                                                                                   |
| ----------------- | ------------------------------------------------------------------------------------------------------ |
| **Backend**       | **Java 21**, **Spring Boot 3**, Spring Security, Spring Data JPA, Hibernate                             |
| **Database**      | **PostgreSQL 16**, **Flyway** (for database migrations)                                                |
| **Frontend**      | **Thymeleaf**, HTML5, CSS3 (with variables), Vanilla JavaScript (ES6+), Yandex Maps API                |
| **Containerization**| **Docker** & **Docker Compose** (with multi-stage builds)                                              |
| **Security**      | Spring Security, **Bucket4j** (for Rate Limiting), Content Security Policy (CSP)                       |
| **Core Libraries**  | **ZXing** (QR Code generation), **libphonenumber** (phone number validation & formatting)              |
| **Build Tool**    | **Maven**                                                                                              |

## 🚀 Getting Started

Follow these instructions to get the project up and running locally.

### Prerequisites
*   [Git](https://git-scm.com/)
*   [JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
*   [Docker](https://www.docker.com/products/docker-desktop/) & [Docker Compose](https://docs.docker.com/compose/install/)

### 1. Clone the Repository
```bash
git clone https://github.com/kaurami/WEMS.git
cd WEMS
```

### 2. Configure the Application

Create a file named `.env` in the root of the project directory and paste the following content into it. **You must modify the credentials and token values.**

```dotenv
# WEMS Configuration File
# -----------------------------------------------------
# Please fill in your actual data for the variables below.

# --- System & Database Credentials ---
# Use strong, unique passwords in production.
DB_NAME=wems_db
DB_USER=wems_user
DB_PASSWORD=your_secret_db_password
SERVER_FORWARD_HEADERS_STRATEGY=framework

# --- Admin Panel Credentials ---
# Use a strong, unique password for the admin panel.
ADMIN_USER=admin
ADMIN_PASS=your_secure_admin_password

# --- Telegram Bot Credentials ---
# Get your bot token from @BotFather on Telegram.
TELEGRAM_BOT_TOKEN=1234567890:AABAbcdeFGHIjklmnOPQRstuvWXYZ12345
# Get your personal chat ID from a bot like @userinfobot on Telegram.
TELEGRAM_GROOM_CHAT_ID=123456789
TELEGRAM_BRIDE_CHAT_ID=987654321

# --- Event Details ---
GROOM_NAME=Ivan
BRIDE_NAME=Maria
EVENT_TIME_ZONE=Europe/Moscow
EVENT_DATE_TIME=2031-05-19T16:00:00
CONFIRMATION_DEADLINE=2030-12-01

# --- Content & Text Snippets ---
GREETING_TEXT=We invite you to share this joyful day with us!
INVITATION_TEXT=With great joy and excitement in our hearts, we would like to invite you to the most important event of our lives – our wedding!
WISHES_FROM_COUPLE=Please no “Bitter!” chants...;We love kids, but...;Instead of flowers, please consider giving...

# --- Venue & Location ---
VENUE_NAME=Your Wedding Venue Name
VENUE_ADDRESS=City, Street Address, Building
VENUE_LATITUDE=25.9874
VENUE_LONGITUDE=25.6124
MAP_ZOOM=13

# --- Contacts & Links ---
GROOM_PHONE=+10000000000
BRIDE_PHONE=+10000000001
TELEGRAM_GROUP_URL=https://t.me/your_wedding_chat

# --- Dress Code ---
DRESS_CODE_TEXT=We would be happy to see you in formal attire. Preferred color palette: pastel tones.
DRESS_CODE_PALETTE="#F5EFE6,#A67B5B,#B0B0B0,#A8D0E6,#1C2E4A"

# --- External Asset Locations (CDN) ---
# The base URL where your static files (images, music) are hosted.
# Example using GitHub Pages: https://your-username.github.io/your-assets-repo/
ASSETS_BASE_URL=https://your-username.github.io/my-assets/

ASSETS_MAIN_BACKGROUND=images/background-main.jpg
ASSETS_VENUE_PHOTO=images/venue-exterior.png
ASSETS_BACKGROUND_MUSIC=audio/theme-song.mp3
```

### 3. Run the Application
The easiest way to run the entire stack is with Docker Compose.
```bash
docker-compose up --build
```
This command will build the app, start the application and database containers, and apply migrations. The application will be available at `http://localhost:8088`.

## ⚙️ Configuration Variables

All customizable parameters are located in a `.env` file in the project's root directory.

| Variable                  | Description                                                                                             | Example                                       |
| ------------------------- | ------------------------------------------------------------------------------------------------------ |-----------------------------------------------|
| **`Database & System`**   |                                                                                                        |                                               |
| `DB_NAME`                 | The name for the PostgreSQL database schema.                                                           | `wems_db`                                     |
| `DB_USER`                 | The username for the PostgreSQL database.                                                              | `wems_user`                                   |
| `DB_PASSWORD`             | The password for the PostgreSQL user. **Use a strong password.**                                       | `dfGdf34Rf2`                                  |
| `SERVER_FORWARD_HEADERS_STRATEGY`| Strategy for handling forwarded headers when behind a proxy (e.g., Nginx).                       | `framework`                                   |
| **`Admin Panel`**         |                                                                                                        |                                               |
| `ADMIN_USER`              | The username for accessing the `/admin` panel.                                                         | `admin`                                       |
| `ADMIN_PASS`              | The password for the admin panel. **Use a strong password.**                                           | `your_secure_password`                        |
| **`Telegram Bot`**        |                                                                                                        |                                               |
| `TELEGRAM_BOT_TOKEN`      | Your Telegram Bot's API token, obtained from `@BotFather`.                                             | `7742685719:AAF...`                           |
| `TELEGRAM_GROOM_CHAT_ID`  | The chat ID where notifications will be sent (e.g., Groom's). Get it from `@userinfobot`.              | `322155569`                                   |
| `TELEGRAM_BRIDE_CHAT_ID`  | (Optional) A second chat ID for notifications (e.g., Bride's).                                         | `322155570`                                   |
| **`Event Details`**       |                                                                                                        |                                               |
| `GROOM_NAME`              | The groom's first name.                                                                                | `Ivan`                                        |
| `BRIDE_NAME`              | The bride's first name.                                                                                | `Maria`                                       |
| `EVENT_TIME_ZONE`         | The IANA timezone ID for the event. **Crucial for the countdown timer.**                               | `Asia/Yekaterinburg`                          |
| `EVENT_DATE_TIME`         | The exact date and time of the event in `YYYY-MM-DDTHH:MM:SS` format.                                  | `2031-05-19T16:00:00`                         |
| `CONFIRMATION_DEADLINE`   | The last date for guests to RSVP, in `YYYY-MM-DD` format.                                              | `2030-12-01`                                  |
| **`Content & Text`**      |                                                                                                        |                                               |
| `GREETING_TEXT`           | The main greeting text on the hero section of the invitation.                                          | `We invite you to share this joyful day...`   |
| `INVITATION_TEXT`         | The main invitation text block.                                                                        | `With great joy... we invite you...`          |
| `WISHES_FROM_COUPLE`      | Semicolon-separated list of wishes or important notes (e.g., about gifts, children).                   | `No “Bitter!” chants...;We love kids, but...` |
| `DRESS_CODE_TEXT`         | A text description of the event's dress code.                                                          | `We would be happy to see you in formal...`   |
| `DRESS_CODE_PALETTE`      | A comma-separated list of hex color codes for the visual dress code palette.                           | `"#F5EFE6,#A67B5B,#B0B0B0"`                   |
| **`Venue & Location`**    |                                                                                                        |                                               |
| `VENUE_NAME`              | The name of the event location (e.g., restaurant, hotel).                                              | `Your Wedding Venue Name`                     |
| `VENUE_ADDRESS`           | The full street address of the venue.                                                                  | `City, Street Address, Building`              |
| `VENUE_LATITUDE`          | The latitude coordinate for the Yandex Map.                                                            | `00.000000`                                   |
| `VENUE_LONGITUDE`         | The longitude coordinate for the Yandex Map.                                                           | `00.000000`                                   |
| `MAP_ZOOM`                | The default zoom level for the map (e.g., 13-16).                                                      | `13`                                          |
| **`Contacts & Links`**    |                                                                                                        |                                               |
| `GROOM_PHONE`             | Groom's contact phone number in international format.                                                  | `+10000000000`                                |
| `BRIDE_PHONE`             | Bride's contact phone number in international format.                                                  | `+10000000001`                                |
| `TELEGRAM_GROUP_URL`      | The invite link for the guest's Telegram group, used for the QR code.                                  | `https://t.me/your_wedding_chat`              |
| **`External Assets (CDN)`**|                                                                                                       |                                               |
| `ASSETS_BASE_URL`         | The base URL of your CDN or static file hosting (e.g., GitHub Pages).                                  | `https://your-username.github.io/my-assets/`  |
| `ASSETS_MAIN_BACKGROUND`  | Path to the main background image, relative to `ASSETS_BASE_URL`.                                      | `images/background-main.jpg`                  |
| `ASSETS_VENUE_PHOTO`      | Path to the venue photo, relative to `ASSETS_BASE_URL`.                                                | `images/venue-exterior.png`                   |
| `ASSETS_BACKGROUND_MUSIC` | Path to the background music file, relative to `ASSETS_BASE_URL`.                                      | `audio/theme-song.mp3`                        |

## 🔧 Advanced Configuration

### Setting up a Custom Icon CDN

WEMS can dynamically load icon packs for the "Event Timeline" feature from an external CDN (e.g., a public GitHub repository configured with GitHub Pages). This allows for deep customization without modifying the application code.

To enable this, your asset host must follow a specific directory structure and include manifest files.

#### 1. Directory Structure
On your CDN or static file host, create a directory structure like this:
```
/images/icons/
├── master-manifest.json
├── minimalist/
│   ├── icon1.svg
│   ├── icon2.png
│   └── manifest.json
└── elegant/
    ├── ring.svg
    ├── cake.svg
    └── manifest.json
```
-   **`/images/icons/`**: The root directory for all icon packs.
-   **`master-manifest.json`**: The main catalog file that lists all available icon packs.
-   **`minimalist-pack/`, `elegant-pack/`**: Individual directories, each representing a single icon pack.
-   **`manifest.json`** (inside each pack directory): Lists all the icon filenames within that specific pack.

#### 2. Create the Manifest Files
You must create the two types of JSON files manually.

**A. The Master Manifest (`master-manifest.json`)**

This file acts as the main catalog. The application fetches it to know which icon packs are available to choose from in the admin panel.

*   **Location:** `/images/icons/master-manifest.json`
*   **Content:** A JSON array of objects. Each object has a `name` (for display in the dropdown) and a `folder` (the directory name).

**Example `master-manifest.json`:**
```json
[
    {
        "name": "Minimalist",
        "folder": "minimalist"
    },
    {
        "name": "Elegant",
        "folder": "elegant"
    }
]
```

**B. The Icon Pack Manifest (`manifest.json`)**

Inside *each* icon pack directory, you must create a `manifest.json` file. This file tells the application which image files belong to that pack.

*   **Location:** `/images/icons/[your-pack-name]/manifest.json`
*   **Content:** A JSON object with a single key, `icons`, which contains a simple array of the filenames of your icons.

**Example `manifest.json` for the `elegant-pack` directory:**
```json
{
    "icons": [
        "ring.svg",
        "cake.svg",
        "couple.png",
        "dinner.png",
        "envelope.svg",
        "glass.png",
        "heart.svg"
    ]
}
```

#### 3. Final Step: Configure the Base URL
After setting up your CDN with the correct structure and manifest files, ensure the `ASSETS_BASE_URL` variable in your `.env` file points to the root URL where your assets are hosted.

**Example in `.env`:**
```dotenv
ASSETS_BASE_URL=https://your-username.github.io/your-assets-repo/
```
The application will then be able to find your icons at paths like `https://your-username.github.io/your-assets-repo/images/icons/elegant-pack/ring.svg`.


## 🤝 Contributing
Contributions are welcome! Please feel free to open an issue or submit a pull request. For more details, see [CONTRIBUTING.md](CONTRIBUTING.md).

## 📜 License
This project is licensed under the **Apache 2.0 License**. See the [LICENSE](LICENSE) file for details.
