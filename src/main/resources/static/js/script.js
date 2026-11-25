document.addEventListener("DOMContentLoaded", function () {
    const reservationForm = document.getElementById("reservationForm");
    const formMessage = document.getElementById("formMessage");
    const reservationDate = document.getElementById("reservationDate");
    const facilityContainer = document.getElementById("facilityContainer");
    const facilitySelect = document.getElementById("facility");

    let facilities = [];
    
    // Set the earliest allowed reservation date to today.
    const today = new Date();
    const localToday = new Date(
        today.getTime() - today.getTimezoneOffset() * 60000
    ).toISOString().split("T")[0];

    reservationDate.min = localToday;

    // Load facilities from the Java backend.
    loadFacilities();

    async function loadFacilities() {
        try {
            const response = await fetch("/api/facilities");

            if (!response.ok) {
                throw new Error("Could not retrieve facilities.");
            }

            facilities = await response.json();

            displayFacilities(facilities);
            populateFacilityDropdown(facilities);
        } catch (error) {
            console.error("Facility loading error:", error);

            facilityContainer.textContent =
                "Unable to load facilities. Please try again later.";

            facilitySelect.replaceChildren(
                new Option("Unable to load facilities", "")
            );
        }
    }

    // Display database facilities as cards.
    function displayFacilities(items) {
        facilityContainer.replaceChildren();

        if (items.length === 0) {
            facilityContainer.textContent =
                "No facilities have been added yet.";
            return;
        }

        items.forEach(function (facility) {
            const card = document.createElement("article");
            card.className = "facility-card";

            const icon = document.createElement("div");
            icon.className = "facility-icon";
            icon.textContent = getFacilityIcon(facility.name);

            const title = document.createElement("h3");
            title.textContent = facility.name;

            const description = document.createElement("p");
            description.textContent =
                facility.description || "No description available.";

            const location = document.createElement("p");
            location.textContent =
                "Location: " + (facility.location || "Not specified");

            const capacity = document.createElement("p");
            capacity.textContent =
                "Capacity: " + facility.capacity + " people";

            const availability = document.createElement("p");
            availability.textContent = facility.available
                ? "Available for requests"
                : "Currently unavailable";

            const reserveLink = document.createElement("a");
            reserveLink.href = "#reservation";
            reserveLink.textContent = "Reserve this facility →";

            reserveLink.addEventListener("click", function () {
                facilitySelect.value = String(facility.id);
            });

            card.append(
                icon,
                title,
                description,
                location,
                capacity,
                availability,
                reserveLink
            );

            facilityContainer.appendChild(card);
        });
    }

    // Fill the reservation form dropdown.
    function populateFacilityDropdown(items) {
        facilitySelect.replaceChildren(
            new Option("Choose a facility", "")
        );

        items.forEach(function (facility) {
            const option = new Option(
                facility.name,
                String(facility.id)
            );

            if (!facility.available) {
                option.disabled = true;
                option.textContent += " (Unavailable)";
            }

            facilitySelect.appendChild(option);
        });
    }

    // Choose an icon based on the facility name.
    function getFacilityIcon(name) {
        const facilityName = (name || "").toLowerCase();

        if (facilityName.includes("computer") ||
            facilityName.includes("laboratory")) {
            return "💻";
        }

        if (facilityName.includes("gym") ||
            facilityName.includes("sport")) {
            return "🏀";
        }

        if (facilityName.includes("conference") ||
            facilityName.includes("meeting")) {
            return "🤝";
        }

        if (facilityName.includes("audio") ||
            facilityName.includes("visual")) {
            return "🎥";
        }

        if (facilityName.includes("classroom")) {
            return "🏫";
        }

        return "🏢";
    }

    // Validate the form. Actual database submission comes later.
    reservationForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const studentName =
            document.getElementById("studentName").value.trim();

        const studentEmail =
            document.getElementById("studentEmail").value.trim();

        const selectedFacility = facilities.find(function (item) {
            return String(item.id) === facilitySelect.value;
        });

        const selectedDate = reservationDate.value;

        const reservationTime =
            document.getElementById("reservationTime").value;

        const purpose =
            document.getElementById("purpose").value.trim();

        if (
            !studentName ||
            !studentEmail ||
            !selectedFacility ||
            !selectedDate ||
            !reservationTime ||
            !purpose
        ) {
            showMessage("Please complete all fields.", false);
            return;
        }

        if (!selectedFacility.available) {
            showMessage(
                "This facility is currently unavailable.",
                false
            );
            return;
        }

        if (selectedDate < localToday) {
            showMessage(
                "Please select today or a future date.",
                false
            );
            return;
        }

        showMessage(
            "Thank you, " + studentName +
            "! Your request for " + selectedFacility.name +
            " is ready. Saving reservations to the database is our next step.",
            true
        );

        console.log("Reservation form details:", {
            studentName,
            studentEmail,
            facilityId: selectedFacility.id,
            selectedDate,
            reservationTime,
            purpose
        });
    });

    function showMessage(message, success) {
        formMessage.textContent = message;
        formMessage.style.color = success ? "#267653" : "#b42318";
    }
});
