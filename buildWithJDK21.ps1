function Find-Jdk21 {
    $jdkKey = "HKLM:\SOFTWARE\JavaSoft\JDK"
    if (Test-Path $jdkKey) {
        $jdkVersions = Get-ChildItem -Path $jdkKey
        foreach ($version in $jdkVersions) {
            if ($version.PSChildName -like "21*") {
                $jdkHome = Get-ItemProperty -Path "$jdkKey\$($version.PSChildName)" | Select-Object -ExpandProperty JavaHome
                return $jdkHome
            }
        }
    }
    return $null
}

$jdk21Path = Find-Jdk21

if ($jdk21Path) {
    Write-Host "JDK 21 found at: $jdk21Path"
    $env:JAVA_HOME = $jdk21Path
    Write-Host "JAVA_HOME set to: $env:JAVA_HOME"
    $scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
    Set-Location $scriptPath
    Write-Host "Running 'mvn clean package' in $scriptPath"
    mvn clean package
} else {
    Write-Host "JDK 21 not found in the registry."
}
