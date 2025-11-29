// Get references to our HTML elements
const getVerseBtn = document.getElementById('getVerseBtn');
const verseCard = document.getElementById('verseCard');
const verseReference = document.getElementById('verseReference');
const verseText = document.getElementById('verseText');
const loading = document.getElementById('loading');

// Add click event listener to the button
getVerseBtn.addEventListener('click', async () => {
    // Hide verse card and show loading
    verseCard.classList.add('hidden');
    loading.classList.remove('hidden');
    getVerseBtn.disabled = true;

    try {
        // Call our REST API
        const response = await fetch('/api/verse');

        if (!response.ok) {
            throw new Error('Failed to fetch verse');
        }

        // Parse the JSON response
        const verse = await response.json();

        // Update the card with verse data
        verseReference.textContent = verse.reference;
        verseText.textContent = verse.text;

        // Hide loading and show verse card
        loading.classList.add('hidden');
        verseCard.classList.remove('hidden');

    } catch (error) {
        console.error('Error:', error);
        alert('Sorry, could not fetch verse. Please try again!');
        loading.classList.add('hidden');
    } finally {
        getVerseBtn.disabled = false;
    }
});

// Optional: Load a verse when page first loads
window.addEventListener('load', () => {
    console.log('📖 Bible Verse App Ready!');
});