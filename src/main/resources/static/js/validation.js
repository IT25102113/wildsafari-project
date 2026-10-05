/**
 * Wildlife Safari Trip Management System
 * Interactive Client-Side Validation & Live Calculator
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Phone number validation (must be exactly 10 digits)
    const phoneInputs = document.querySelectorAll('input[type="tel"], input[name*="phone"], input[name*="Phone"], input[name*="contact"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', (e) => {
            let val = e.target.value.replace(/\D/g, ''); // strip non-numeric
            if (val.length > 10) val = val.substring(0, 10);
            e.target.value = val;

            const errorEl = document.getElementById(input.id + '-error') || input.nextElementSibling;
            if (val.length === 10) {
                input.classList.remove('is-invalid');
                input.classList.add('is-valid');
                if (errorEl && errorEl.classList.contains('validation-msg')) {
                    errorEl.textContent = '';
                }
            } else {
                input.classList.remove('is-valid');
                input.classList.add('is-invalid');
                if (errorEl && errorEl.classList.contains('validation-msg')) {
                    errorEl.textContent = 'Phone number must be exactly 10 digits (e.g. 0771234567).';
                }
            }
        });
    });

    // 2. Email validation (RFC pattern / standard gmail format)
    const emailInputs = document.querySelectorAll('input[type="email"], input[name*="email"], input[name*="Email"]');
    const emailPattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
    emailInputs.forEach(input => {
        input.addEventListener('blur', (e) => {
            const val = e.target.value.trim();
            const errorEl = document.getElementById(input.id + '-error') || input.nextElementSibling;
            if (emailPattern.test(val)) {
                input.classList.remove('is-invalid');
                input.classList.add('is-valid');
                if (errorEl && errorEl.classList.contains('validation-msg')) {
                    errorEl.textContent = '';
                }
            } else if (val.length > 0) {
                input.classList.remove('is-valid');
                input.classList.add('is-invalid');
                if (errorEl && errorEl.classList.contains('validation-msg')) {
                    errorEl.textContent = 'Please provide a valid email address (e.g. yourname@gmail.com).';
                }
            }
        });
    });

    // 3. Dynamic Safari Price Calculator & Strict Validation on Booking Form
    const participantInput = document.getElementById('bookingParticipants');
    const tripDateInput = document.getElementById('bookingTripDate');
    const totalPriceDisplay = document.getElementById('calculatedTotalPrice');
    const basePriceEl = document.getElementById('pkgBasePrice');
    const multiplierEl = document.getElementById('pkgMultiplier');
    const safariBookingForm = document.getElementById('safariBookingForm');

    // Enforce min date attribute to today across all tripDate inputs
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    document.querySelectorAll('input[type="date"][name*="tripDate"], input[type="date"][name*="Date"]').forEach(input => {
        if (!input.getAttribute('min')) {
            input.setAttribute('min', todayStr);
        }
    });

    // Validate Trip Date instantly
    const validateTripDate = (input) => {
        if (!input) return true;
        const val = input.value;
        const errorEl = document.getElementById(input.id + '-error') || input.nextElementSibling;
        if (!val) {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = 'Please choose a safari expedition date.';
            }
            return false;
        }

        const selectedDate = new Date(val + 'T00:00:00');
        if (selectedDate < today) {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = '⚠️ Cannot book past dates! Please select today or a future expedition date.';
            }
            return false;
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = '';
            }
            return true;
        }
    };

    // Validate Participant Count instantly
    const validateParticipants = (input) => {
        if (!input) return true;
        const max = parseInt(input.dataset.max || input.getAttribute('max') || '10', 10);
        const errorEl = document.getElementById(input.id + '-error') || input.nextElementSibling;
        const rawVal = input.value.trim();
        const val = parseInt(rawVal, 10);

        if (rawVal === '' || isNaN(val) || val < 1) {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = '⚠️ Passenger count cannot be 0 or negative. Minimum 1 passenger required.';
            }
            return false;
        } else if (val > max) {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = `⚠️ Exceeds max group limit of ${max} passengers for this package.`;
            }
            return false;
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
            if (errorEl && errorEl.classList.contains('validation-msg')) {
                errorEl.textContent = '';
            }
            return true;
        }
    };

    // Prevent typing negative signs on number inputs
    document.querySelectorAll('input[type="number"]').forEach(numInput => {
        numInput.addEventListener('keydown', (e) => {
            if (e.key === '-' || e.key === '+' || e.key === 'e' || e.key === 'E') {
                e.preventDefault();
            }
        });
    });

    if (tripDateInput) {
        tripDateInput.addEventListener('change', () => validateTripDate(tripDateInput));
        tripDateInput.addEventListener('input', () => validateTripDate(tripDateInput));
    }

    if (participantInput) {
        participantInput.addEventListener('input', () => validateParticipants(participantInput));
        participantInput.addEventListener('change', () => validateParticipants(participantInput));
    }

    // Dynamic Price Calculator
    if (participantInput && basePriceEl && totalPriceDisplay) {
        const calculatePrice = () => {
            const isDateValid = validateTripDate(tripDateInput);
            const isPaxValid = validateParticipants(participantInput);

            const basePrice = parseFloat(basePriceEl.value || basePriceEl.textContent) || 0;
            const participants = parseInt(participantInput.value, 10);
            const safePax = (isNaN(participants) || participants < 1) ? 1 : participants;
            let multiplier = 1.0;

            if (tripDateInput && tripDateInput.value) {
                const date = new Date(tripDateInput.value + 'T00:00:00');
                const month = date.getMonth() + 1; // 1-12
                // July (7), August (8), Dec (12), Jan (1) are Peak Seasons
                if (month === 7 || month === 8 || month === 12 || month === 1) {
                    multiplier = parseFloat(multiplierEl ? multiplierEl.value : 1.25);
                    const peakBadge = document.getElementById('peakSeasonNotice');
                    if (peakBadge) peakBadge.classList.remove('d-none');
                } else {
                    const peakBadge = document.getElementById('peakSeasonNotice');
                    if (peakBadge) peakBadge.classList.add('d-none');
                }
            }

            const total = basePrice * safePax * multiplier;
            totalPriceDisplay.textContent = 'LKR ' + total.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        };

        participantInput.addEventListener('input', calculatePrice);
        if (tripDateInput) tripDateInput.addEventListener('change', calculatePrice);
        calculatePrice();
    }

    // Block Form Submission if Date is in Past or Participants Invalid
    if (safariBookingForm) {
        safariBookingForm.addEventListener('submit', (e) => {
            const isDateOk = validateTripDate(tripDateInput);
            const isPaxOk = validateParticipants(participantInput);

            if (!isDateOk || !isPaxOk) {
                e.preventDefault();
                e.stopPropagation();
                if (!isDateOk && tripDateInput) tripDateInput.focus();
                else if (!isPaxOk && participantInput) participantInput.focus();
            }
        });
    }

    // 4. Mock Credit Card Input Auto-formatter
    const cardInput = document.getElementById('cardInput');
    if (cardInput) {
        cardInput.addEventListener('input', (e) => {
            let val = e.target.value.replace(/\D/g, '').substring(0, 16);
            let formatted = val.match(/.{1,4}/g)?.join(' ') || val;
            e.target.value = formatted;
        });
    }

    const expiryInput = document.getElementById('expiryInput');
    if (expiryInput) {
        expiryInput.addEventListener('input', (e) => {
            let val = e.target.value.replace(/\D/g, '').substring(0, 4);
            if (val.length >= 3) {
                e.target.value = val.substring(0, 2) + '/' + val.substring(2);
            } else {
                e.target.value = val;
            }
        });
    }
});
