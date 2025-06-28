@echo off
git add .
git commit -m "auto: 一键推送所有变更"
git push
echo 代码已推送！
pause 