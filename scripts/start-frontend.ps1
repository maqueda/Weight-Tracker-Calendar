$projectRoot = Split-Path -Parent $PSScriptRoot
$frontendPath = Join-Path $projectRoot "frontend"

$defaultNodePath = "C:\Program Files\nodejs"
if ((Test-Path $defaultNodePath) -and ($env:Path -notlike "*$defaultNodePath*")) {
  $env:Path = "$defaultNodePath;$env:Path"
}

Push-Location $frontendPath
try {
  if (-not (Test-Path "node_modules")) {
    npm install
  }

  npm run dev
} finally {
  Pop-Location
}
