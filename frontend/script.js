const sourceCurrency = document.getElementById("sourceCurrency");
const targetCurrency = document.getElementById("targetCurrency");
const amountInput = document.getElementById("amount");
const convertBtn = document.getElementById("convertBtn");
const swapBtn = document.getElementById("swapBtn");
const resultDiv = document.getElementById("result");

// Fetch currency codes from backend
async function populateDropdowns() {
  try {
    const response = await fetch("http://localhost:8080/api/currencies");
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const currencyCodes = await response.json();
    console.log("Fetched currency codes:", currencyCodes);

    sourceCurrency.innerHTML = '<option value="">Select Currency</option>';
    targetCurrency.innerHTML = '<option value="">Select Currency</option>';

    currencyCodes.forEach(code => {
      sourceCurrency.add(new Option(code, code));
      targetCurrency.add(new Option(code, code));
    });

    // Defaults
    sourceCurrency.value = "USD";
    targetCurrency.value = "INR";
  } catch (error) {
    console.error("Failed to load currency codes:", error);
    resultDiv.textContent = "⚠️ Error loading currencies. Please check backend.";
    resultDiv.style.color = "#dc3545";
  }
}

// Swap currencies
swapBtn.addEventListener("click", () => {
  const temp = sourceCurrency.value;
  sourceCurrency.value = targetCurrency.value;
  targetCurrency.value = temp;
});

// Handle conversion
convertBtn.addEventListener("click", async () => {
  const from = sourceCurrency.value;
  const to = targetCurrency.value;
  const amount = amountInput.value;

  if (!from || !to) {
    alert("Please select both currencies");
    return;
  }

  if (!amount || amount <= 0) {
    alert("Please enter a valid amount");
    return;
  }

  resultDiv.textContent = "Converting...";
  convertBtn.disabled = true;
  swapBtn.disabled = true;

  try {
    const response = await fetch("http://localhost:8080/api/convert", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fromCurrency: from,
        toCurrency: to,
        amount: parseInt(amount, 10)
      })
    });

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const data = await response.json();
    console.log("Conversion response:", data);

    if (data.error) throw new Error(data.error);

    const convertedAmount = data.convertedAmount.toFixed(2);
    resultDiv.textContent = `${from} ${amount} = ${to} ${convertedAmount}`;
    resultDiv.style.color = "#28a745";
  } catch (error) {
    console.error("Conversion error:", error);
    resultDiv.textContent = "❌ Error converting currency. Please try again.";
    resultDiv.style.color = "#dc3545";
  } finally {
    convertBtn.disabled = false;
    swapBtn.disabled = false;
  }
});

// Enter key shortcut
amountInput.addEventListener("keypress", (e) => {
  if (e.key === "Enter") convertBtn.click();
});

// Initial load
populateDropdowns();
