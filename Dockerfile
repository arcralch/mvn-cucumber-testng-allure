########################################################################
# Dockerfile – Entorno completo para Selenium + Cucumber + TestNG
# Incluye: Java 11, Maven, Google Chrome, Mozilla Firefox,
#          ChromeDriver, GeckoDriver y Xvfb (display virtual)
########################################################################

FROM ubuntu:22.04

# Evitar prompts interactivos durante la instalación de paquetes
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=America/Mexico_City

# ── 1. Dependencias base del sistema ────────────────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    wget \
    unzip \
    gnupg2 \
    ca-certificates \
    software-properties-common \
    apt-transport-https \
    xvfb \
    libxi6 \
    libgconf-2-4 \
    python3 \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libc6 \
    libcairo2 \
    libcups2 \
    libdbus-1-3 \
    libexpat1 \
    libfontconfig1 \
    libgbm1 \
    libglib2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libu2f-udev \
    libvulkan1 \
    libx11-6 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxi6 \
    libxrandr2 \
    libxrender1 \
    libxss1 \
    libxtst6 \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# ── 2. Java 11 (OpenJDK Temurin) ─────────────────────────────────────
RUN wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor \
    | tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null \
    && echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" \
    | tee /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y temurin-11-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/temurin-11-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# ── 3. Maven ──────────────────────────────────────────────────────────
ARG MAVEN_VERSION=3.9.6
RUN wget -q "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
    && tar -xzf "apache-maven-${MAVEN_VERSION}-bin.tar.gz" -C /opt \
    && mv "/opt/apache-maven-${MAVEN_VERSION}" /opt/maven \
    && rm "apache-maven-${MAVEN_VERSION}-bin.tar.gz"

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH="${M2_HOME}/bin:${PATH}"

# ── 4. Google Chrome ──────────────────────────────────────────────────
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub \
    | gpg --dearmor | tee /etc/apt/trusted.gpg.d/google-chrome.gpg > /dev/null \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# ── 5. Mozilla Firefox ────────────────────────────────────────────────
RUN add-apt-repository ppa:mozillateam/ppa -y \
    && apt-get update \
    && apt-get install -y firefox \
    && rm -rf /var/lib/apt/lists/*

# ── 6. GeckoDriver (WebDriver para Firefox) ──────────────────────────
RUN GECKO_VERSION=$(curl -s https://api.github.com/repos/mozilla/geckodriver/releases/latest \
        | grep '"tag_name"' | sed 's/.*"v\([^"]*\)".*/\1/') \
    && wget -q "https://github.com/mozilla/geckodriver/releases/download/v${GECKO_VERSION}/geckodriver-v${GECKO_VERSION}-linux64.tar.gz" \
    && tar -xzf "geckodriver-v${GECKO_VERSION}-linux64.tar.gz" \
    && mv geckodriver /usr/local/bin/ \
    && chmod +x /usr/local/bin/geckodriver \
    && rm "geckodriver-v${GECKO_VERSION}-linux64.tar.gz"

# ── 7. Directorio de trabajo ──────────────────────────────────────────
WORKDIR /app

# ── 8. Cache de dependencias Maven (pre-descarga) ─────────────────────
# Copia solo el pom.xml primero para aprovechar el cache de capas Docker
COPY pom.xml .
RUN mvn dependency:go-offline -q || true

# ── 9. Copiar el código fuente del proyecto ───────────────────────────
COPY src ./src
COPY testng.xml .

# ── 10. Variables de entorno por defecto ──────────────────────────────
ENV HEADLESS=true
ENV VIDEO=false
ENV BROWSER=CHROME
ENV ENV=qa
ENV DISPLAY=:99

# ── 11. Script de entrada: inicia Xvfb y ejecuta los tests ────────────
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["mvn", "test"]
