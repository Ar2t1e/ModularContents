const { execSync } = require('child_process');
try { execSync('taskkill /F /IM java.exe'); } catch(e){}
try {
    execSync('gradlew build', {stdio: 'inherit'});
    console.log("Success");
} catch(e) {
    console.log("Failed");
}
