import * as skinview3d from 'skinview3d';

async function initSkinViewer() {
    const canvas = document.getElementById('skin_container') as HTMLCanvasElement;
    const userInfoDiv = document.getElementById('user-info');
    const statusMessageDiv = document.getElementById('status-message');

    if (!canvas || !userInfoDiv || !statusMessageDiv) {
        console.error("Required HTML elements not found.");
        if (statusMessageDiv) statusMessageDiv.innerHTML = `<p class="text-danger">Error: Page elements missing.</p>`;
        return;
    }

    const username = localStorage.getItem("username");

    if (!username) {
        console.error("Username cookie not found.");
        userInfoDiv.innerHTML = `<p class="text-danger">Username not found. Please log in.</p>`;
        statusMessageDiv.innerHTML = `<p class="text-warning">Cannot load skin without username.</p>`;
        // Hide canvas if no username
        canvas.style.display = 'none';
        return;
    }

    const skinViewer = new skinview3d.SkinViewer({
        canvas: canvas,
        width: 300,
        height: 400,
    });

    // Configure the viewer
    skinViewer.fov = 70;
    skinViewer.zoom = 0.8;
    skinViewer.autoRotate = false;
    skinViewer.animation = new skinview3d.WalkingAnimation();
    skinViewer.animation.speed = 1;
    skinViewer.cameraLight.intensity = 3;
    skinViewer.globalLight.intensity = 2;
    
    skinViewer.nameTag = "Shift_Sad";

    // Fetch skin URL
    let skinUrl: string | null = null;
    try {
        const skinResponse = await fetch(`http://127.0.0.1:3000/skins/skin/${username}`);
        if (skinResponse.ok) {
            const skinData = await skinResponse.json();
            if (skinData.success && skinData.data) {
                skinUrl = skinData.data;
            } else {
                console.warn(`Could not fetch skin for ${username}: ${skinData.message}`);
            }
        } else {
            console.error(`Error fetching skin for ${username}: ${skinResponse.statusText}`);
        }
    } catch (error) {
        console.error(`Network error fetching skin for ${username}:`, error);
    }

    // Fetch cape URL
    let capeUrl: string | null = null;
    try {
        const capeResponse = await fetch(`http://127.0.0.1:3000/skins/cape/${username}`);
        if (capeResponse.ok) {
            const capeData = await capeResponse.json();
            if (capeData.success && capeData.data) {
                capeUrl = capeData.data;
            } else {
                console.warn(`Could not fetch cape for ${username}: ${capeData.message}`);
            }
        } else {
            console.error(`Error fetching cape for ${username}: ${capeResponse.statusText}`);
        }
    } catch (error) {
        console.error(`Network error fetching cape for ${username}:`, error);
    }

    let loadedItems = 0;
    let erroredItems = 0;

    if (skinUrl) {
        console.log(`Loading skin from: ${skinUrl}`);
        skinViewer.loadSkin(skinUrl)
            .then(() => {
                console.log("Skin loaded successfully.");
                loadedItems++;
                updateStatus();
            })
            .catch(err => {
                console.error("Error loading skin into viewer:", err);
                erroredItems++;
                updateStatus(true, "skin");
            });
    } else {
        console.warn("No skin URL found. Player might not have a custom skin or API error.");
        skinViewer.loadSkin(null); // Clears skin or loads default if skinview3d has one
        erroredItems++;
        updateStatus(true, "skin");
    }

    if (capeUrl) {
        console.log(`Loading cape from: ${capeUrl}`);
        skinViewer.loadCape(capeUrl)
            .then(() => {
                console.log("Cape loaded successfully.");
                loadedItems++;
                updateStatus();
            })
            .catch(err => {
                console.error("Error loading cape into viewer:", err);
                erroredItems++;
                updateStatus(true, "cape");
            });
    } else {
        console.info("No cape URL found. Player might not have a cape.");
        skinViewer.loadCape(null); // Clears cape
    }

    function updateStatus(errorOccurred: boolean = false, itemType?: string) {
        if (errorOccurred) {
            statusMessageDiv!!.innerHTML = `<p class="text-danger">Error loading ${itemType || 'assets'}. Check console for details.</p>`;
            if (erroredItems > 0 && loadedItems === 0 && !skinUrl && !capeUrl) {
                 statusMessageDiv!!.innerHTML = `<p class="text-warning">Could not find skin or cape for ${username}. Displaying default model.</p>`;
            }
        } else if (loadedItems > 0 || (skinUrl === null && capeUrl === null) ) { // Consider no skin/cape as "loaded" default
            statusMessageDiv!!.innerHTML = `<p class="text-success"><i class="fas fa-check-circle"></i> Player model loaded!</p>`;
        }
    }

    // Initial call to updateStatus in case no skin/cape URLs are found at all
    if (!skinUrl && !capeUrl) {
        updateStatus(true, "skin and cape");
    }
}

export default initSkinViewer;