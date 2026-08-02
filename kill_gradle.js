const { execSync } = require('child_process');

try {
    const jpsOutput = execSync('jps').toString();
    const lines = jpsOutput.split('\n');
    for (const line of lines) {
        if (line.includes('GradleDaemon')) {
            const pid = line.split(' ')[0];
            console.log(`Killing GradleDaemon ${pid}`);
            execSync(`taskkill /F /PID ${pid}`);
        }
    }
} catch (e) {
    console.log("No java processes found or error:", e.message);
}
