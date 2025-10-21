"use client";
import React, { useState } from "react";
import { useParams, useRouter } from "next/navigation";

export default function ReplyPage() {
  const { id } = useParams();
  const router = useRouter();
  const [reply, setReply] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const res = await fetch(`http://localhost:8080/api/feedback/reply/${id}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ reply }),
      });

      if (res.ok) {
        alert("Reply sent successfully!");
        router.push("/contact-admin");
      } else {
        alert("Failed to send reply.");
      }
    } catch (error) {
      console.error("Error sending reply:", error);
      alert("An error occurred.");
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Reply to Feedback #{id}</h1>
      <form onSubmit={handleSubmit}>
        <textarea
          className="w-full border p-3 rounded mb-4"
          rows={6}
          placeholder="Type your reply message here..."
          value={reply}
          onChange={(e) => setReply(e.target.value)}
        />
        <div className="flex gap-4">
          <button
            type="submit"
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          >
            Send Reply
          </button>
          <button
            type="button"
            className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
            onClick={() => router.push("/contact-admin")}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
