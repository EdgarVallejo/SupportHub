$ErrorActionPreference = 'Stop'
$logPath = Join-Path $env:TEMP 'supporthub-wsl-install.log'
Start-Transcript -Path $logPath -Force
try {
    $principal = [Security.Principal.WindowsPrincipal]::new([Security.Principal.WindowsIdentity]::GetCurrent())
    if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
        throw 'Ejecuta este script como administrador.'
    }
    winget install --id Microsoft.WSL --exact --source winget --silent --accept-source-agreements --accept-package-agreements --disable-interactivity
    if ($LASTEXITCODE -ne 0) { throw "WinGet WSL: $LASTEXITCODE" }
    Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform -All -NoRestart
    Write-Output 'WSL instalado. Puede ser necesario reiniciar Windows y activar la virtualizacion en BIOS/UEFI.'
} finally {
    Stop-Transcript
}
