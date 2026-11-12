# Detiene el script ante errores de PowerShell y conserva un registro de la instalacion.
$ErrorActionPreference = 'Stop'
$logPath = Join-Path $env:TEMP 'supporthub-wsl-install.log'
Start-Transcript -Path $logPath -Force
try {
    # Las caracteristicas opcionales de Windows exigen una sesion elevada.
    $principal = [Security.Principal.WindowsPrincipal]::new([Security.Principal.WindowsIdentity]::GetCurrent())
    if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
        throw 'Ejecuta este script como administrador.'
    }
    # Descarga e instala exclusivamente el paquete oficial de WSL mediante WinGet.
    winget install --id Microsoft.WSL --exact --source winget --silent --accept-source-agreements --accept-package-agreements --disable-interactivity
    # Los programas nativos informan errores con un codigo de salida independiente.
    if ($LASTEXITCODE -ne 0) { throw "WinGet WSL: $LASTEXITCODE" }
    # Prepara el soporte de maquinas virtuales para WSL 2 sin reiniciar automaticamente.
    Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform -All -NoRestart
    Write-Output 'WSL instalado. Puede ser necesario reiniciar Windows y activar la virtualizacion en BIOS/UEFI.'
} finally {
    # Cierra el registro incluso si alguna instalacion falla.
    Stop-Transcript
}
