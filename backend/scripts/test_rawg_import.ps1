param()
$ErrorActionPreference = "Stop"

$root = "C:\Users\Joaquin\AndroidStudioProjects\Tiendaapp6"
$gradlew = Join-Path $root "gradlew"
$logOut = Join-Path $root "bootrun_tmp.log"
$logErr = Join-Path $root "bootrun_tmp_err.log"

if (Test-Path $logOut) { Remove-Item $logOut -Force }
if (Test-Path $logErr) { Remove-Item $logErr -Force }

$proc = Start-Process -FilePath "cmd.exe" -ArgumentList "/c","$gradlew","-p","backend","bootRun","--console=plain","--no-daemon" -WorkingDirectory $root -RedirectStandardOutput $logOut -RedirectStandardError $logErr -PassThru

$maxAttempts = 30
for ($i = 0; $i -lt $maxAttempts; $i++) {
    Start-Sleep -Seconds 1
    $port = Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue
    if ($port) { break }
}

$bulkStatus = ""
$bulkBody = ""
try {
    $bulkResp = Invoke-WebRequest -Method Post -Uri "http://localhost:8081/api/productos/import/rawg?page=1&pageSize=10&precio=29990&stock=5" -ErrorAction Stop
    $bulkStatus = $bulkResp.StatusCode
    $bulkBody = $bulkResp.Content
} catch {
    $bulkStatus = $_.Exception.Response.StatusCode.value__
    $bulkBody = $_.ErrorDetails.Message
}

$singleStatus = ""
$singleBody = ""
try {
    $singleResp = Invoke-WebRequest -Method Post -ContentType "application/json" -Body '{"precio":29990,"stock":5}' -Uri "http://localhost:8081/api/productos/import/rawg/3498" -ErrorAction Stop
    $singleStatus = $singleResp.StatusCode
    $singleBody = $singleResp.Content
} catch {
    $singleStatus = $_.Exception.Response.StatusCode.value__
    $singleBody = $_.ErrorDetails.Message
}

$products = & curl.exe -s "http://localhost:8081/api/productos"

Stop-Process -Id $proc.Id -Force

Write-Output "--- bootRun stdout tail ---"
Get-Content -Path $logOut -Tail 50
Write-Output "--- bootRun stderr tail ---"
Get-Content -Path $logErr -Tail 50
Write-Output "--- bulk import status/body ---"
Write-Output $bulkStatus
Write-Output $bulkBody
Write-Output "--- single import status/body ---"
Write-Output $singleStatus
Write-Output $singleBody
Write-Output "--- products after calls ---"
Write-Output $products
