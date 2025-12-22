const express = require("express");
const cors = require("cors");

const app = express();
const PORT = 5000;

app.use(cors());
app.use(express.json());

app.post("/chat", (req, res) => {
  const { messages } = req.body;

  if (!messages) {
    return res.status(400).json({ error: "Missing 'messages' in request body" });
  }

  const reply = messages[messages.length - 1] + " 🤖";

  res.json({ reply });
});

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});