########################################################################
# Dockerfile – Selenium + Cucumber + TestNG + Allure
########################################################################

FROM maven:3.9-eclipse-temurin-11

ENV DEBIAN_FRONTEND=noninteractive

# ── 1. Dependencias del sistema ──────────────────────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    wget \
    gnupg2 \
    ca-certificates \
    unzip \
    bzip2 \
    xvfb \
    git \
    libglib2.0-0 \
    libnss3 \
    libgbm1 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libxrender1 \
    libxss1 \
    libxtst6 \
    libdbus-glib-1-2 \
    fonts-liberation \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# ── 2. Google Chrome ──────────────────────────────────────────────────
RUN wget -q -O /tmp/chrome.deb \
    "https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb" \
    && apt-get update \
    && apt-get install -y /tmp/chrome.deb \
    && rm /tmp/chrome.deb \
    && rm -rf /var/lib/apt/lists/*

# ── 3. Mozilla Firefox ───────────────────────────────────────────────
RUN install -d -m 0755 /etc/apt/keyrings \
    && wget -q https://packages.mozilla.org/apt/repo-signing-key.gpg \
    -O /etc/apt/keyrings/packages.mozilla.org.asc \
    && echo "deb [signed-by=/etc/apt/keyrings/packages.mozilla.org.asc] \
    https://packages.mozilla.org/apt mozilla main" \
    | tee /etc/apt/sources.list.d/mozilla.list > /dev/null \
    && printf 'Package: *\nPin: origin packages.mozilla.org\nPin-Priority: 1000\n' \
    | tee /etc/apt/preferences.d/mozilla \
    && apt-get update \
    && apt-get install -y --no-install-recommends firefox \
    && rm -rf /var/lib/apt/lists/*

# ── 4. GeckoDriver ───────────────────────────────────────────────────
RUN GECKO_VERSION=$(curl -s \
    "https://api.github.com/repos/mozilla/geckodriver/releases/latest" \
    | grep '"tag_name"' | sed 's/.*"v\([^"]*\)".*/\1/') \
    && wget -q -O /tmp/geckodriver.tar.gz \
    "https://github.com/mozilla/geckodriver/releases/download/v${GECKO_VERSION}/geckodriver-v${GECKO_VERSION}-linux64.tar.gz" \
    && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin/ \
    && chmod +x /usr/local/bin/geckodriver \
    && rm /tmp/geckodriver.tar.gz

# ── 5. Verificación ──────────────────────────────────────────────────
RUN java -version && mvn --version \
    && google-chrome --version \
    && firefox --version \
    && geckodriver --version | head -1

# ── 6. Clonar repositorio ────────────────────────────────────────────
RUN git clone https://github.com/arcralch/mvn-cucumber-testng-allure.git -b main /app

WORKDIR /app

# ── 7. Descargar dependencias Maven ───────────────────────────────────
RUN mvn dependency:go-offline -q || true

# ── 8. Comando por defecto ───────────────────────────────────────────
CMD ["mvn", "clean", "test"]