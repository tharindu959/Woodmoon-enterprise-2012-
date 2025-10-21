"use client";
import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

type Feedback = {
  id: number;
  name: string;
  email: string;
  message: string;
  date?: string;
};

export default function ContactAdminPage() {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const router = useRouter();

  // Fetch all feedback from Spring Boot backend
  useEffect(() => {
    fetch("http://localhost:8080/api/feedback")
      .then(res => res.json())
      .then(data => setFeedbacks(data))
      .catch(err => console.error("Error loading feedback:", err));
  }, []);

  // Delete feedback
  const handleDelete = async (id: number) => {
    const confirmDelete = confirm("Are you sure you want to delete this feedback?");
    if (!confirmDelete) return;

    try {
      await fetch(`http://localhost:8080/api/feedback/${id}`, { method: "DELETE" });
      setFeedbacks(prev => prev.filter(f => f.id !== id));
    } catch (error) {
      console.error("Error deleting feedback:", error);
    }
  };

  // Go to reply page
  const handleReply = (id: number) => {
    router.push(`/contact-admin/reply/${id}`);
  };

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">Client Feedback Management</h1>

      {feedbacks.length === 0 ? (
        <p className="text-gray-500">No feedback messages available.</p>
      ) : (
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-100">
            <tr>
              <th className="p-3 border">Name</th>
              <th className="p-3 border">Email</th>
              <th className="p-3 border">Message</th>
              <th className="p-3 border">Actions</th>
            </tr>
          </thead>
          <tbody>
            {feedbacks.map(f => (
              <tr key={f.id} className="hover:bg-gray-50">
                <td className="p-3 border">{f.name}</td>
                <td className="p-3 border">{f.email}</td>
                <td className="p-3 border">{f.message}</td>
                <td className="p-3 border text-center">
                  <button
                    className="bg-blue-600 text-white px-3 py-1 rounded mr-2 hover:bg-blue-700"
                    onClick={() => handleReply(f.id)}
                  >
                    Reply
                  </button>
                  <button
                    className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                    onClick={() => handleDelete(f.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
