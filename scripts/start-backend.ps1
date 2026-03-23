$projectRoot = Split-Path -Parent $PSScriptRoot
$backendPath = Join-Path $projectRoot "backend"

if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
  $defaultJdk = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
  if (Test-Path $defaultJdk) {
    $env:JAVA_HOME = $defaultJdk
  }
}

if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
  throw "No se encontró JAVA_HOME. Instala o configura un JDK 21 antes de arrancar el backend."
}

$javaBin = Join-Path $env:JAVA_HOME "bin"
if ($env:Path -notlike "*$javaBin*") {
  $env:Path = "$javaBin;$env:Path"
}

Push-Location $backendPath
try {
  mvn spring-boot:run
} finally {
  Pop-Location
}
