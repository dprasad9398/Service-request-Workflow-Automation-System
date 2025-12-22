const chatBox = document.getElementById("chat-box");
const input = document.getElementById("message-input");
const sendBtn = document.getElementById("send-btn");

sendBtn.addEventListener("click", sendMessage);
input.addEventListener("keypress", (e) => {
  if (e.key === "Enter") sendMessage();
});

async function sendMessage() {
  const message = input.value.trim();
  if (!message) return;

  appendMessage("You", message);
  input.value = "";

  try {
    const res = await fetch("http://localhost:5000/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ messages: [message] })
    });

    const data = await res.json();
    appendMessage("Bot", data.reply);
  } catch (err) {
    appendMessage("Bot", "Error: Unable to connect to server");
  }
}

function appendMessage(sender, message) {
  const msgDiv = document.createElement("div");
  msgDiv.textContent = `${sender}: ${message}`;
  chatBox.appendChild(msgDiv);
  chatBox.scrollTop = chatBox.scrollHeight;
}