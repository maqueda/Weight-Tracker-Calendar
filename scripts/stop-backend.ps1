$listeners = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue

if (-not $listeners) {
  Write-Host "No hay ningún proceso escuchando en el puerto 8080."
  exit 0
}

$stopped = @()
foreach ($listener in $listeners) {
  if ($stopped -contains $listener.OwningProcess) {
    continue
  }

  Stop-Process -Id $listener.OwningProcess -Force
  $stopped += $listener.OwningProcess
}

Write-Host "Backend detenido en el puerto 8080. PID(s): $($stopped -join ', ')"
