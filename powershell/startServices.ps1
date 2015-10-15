Set-Location -Path $PSScriptRoot
cd..

Start-Process activator '"project api" "run 1000"'
#Start-Process activator '"project poll-api" "run 2000"'
#Start-Process activator '"project persistence" "run 3000"'