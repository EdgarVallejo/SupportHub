# Valida los entregables documentales de I-00-02 desde cualquier directorio.
$ErrorActionPreference = 'Stop'
$repositoryRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$adrDirectory = Join-Path $repositoryRoot 'docs\adr'

# Exige las seis decisiones previstas; otros archivos Markdown pueden ser indices.
$expectedFiles = @(
    '0001-separacion-de-servicios.md',
    '0002-outbox-inbox.md',
    '0003-pgvector-embeddings.md',
    '0004-aprobacion-ligada-a-hash.md',
    '0005-publicacion-interna.md',
    '0006-modelo-stub-y-real.md'
)
$requiredSections = @('## Contexto', '## Decision', '## Consecuencias', '## Verificacion')
foreach ($fileName in $expectedFiles) {
    $adrText = Get-Content -LiteralPath (Join-Path $adrDirectory $fileName) -Raw -Encoding UTF8
    # Los encabezados facilitan revisar cada decision con la misma estructura.
    foreach ($section in $requiredSections) {
        if ($adrText -notmatch ('(?m)^' + [regex]::Escape($section) + '\r?$')) {
            throw "Seccion ausente: $fileName -> $section"
        }
    }
    if ($adrText -notmatch '(?m)^- Estado: .+' -or $adrText -notmatch '(?m)^- Trazabilidad: .+') {
        throw "Faltan estado o trazabilidad en $fileName"
    }
}

# Comprueba los enlaces Markdown locales de los ADR y sus documentos de entrada.
# Solo resuelve archivos; no valida fragmentos, URLs externas ni fidelidad semantica.
$docFiles = @(Get-ChildItem -LiteralPath $adrDirectory -Filter '*.md')
foreach ($relativePath in @('README.md', 'docs/backlog.md', 'docs/evidence/I-00-02.md')) {
    $docFiles += Get-Item -LiteralPath (Join-Path $repositoryRoot $relativePath)
}
$checkedLinks = 0
foreach ($docFile in $docFiles) {
    $docText = Get-Content -LiteralPath $docFile.FullName -Raw -Encoding UTF8
    foreach ($linkMatch in [regex]::Matches($docText, '\[[^\]]+\]\(([^)]+)\)')) {
        $linkTarget = $linkMatch.Groups[1].Value
        if ($linkTarget -match '^https?://') { continue }
        $resolvedLink = Join-Path $docFile.DirectoryName ($linkTarget -split '#')[0]
        if (-not (Test-Path -LiteralPath $resolvedLink -PathType Leaf)) {
            throw "Enlace roto: $($docFile.Name) -> $linkTarget"
        }
        $checkedLinks++
    }
}

# Un error produce una excepcion; este mensaje solo aparece si todo lo anterior pasa.
Write-Output "VALIDACION CORRECTA: 6 ADR, 24 secciones obligatorias, $checkedLinks enlaces locales validos."
