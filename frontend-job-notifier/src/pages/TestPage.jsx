import { getUsers, sendTestGmail, addUser } from "../services/apiClient";
import { useState, useEffect } from "react";

const styles = {
    page: {
        minHeight: "100vh",
        background: "linear-gradient(135deg, #0f172a 0%, #1e293b 100%)",
        fontFamily: "'Segoe UI', sans-serif",
        padding: "40px 24px",
        color: "#e2e8f0",
    },
    header: {
        marginBottom: "32px",
    },
    title: {
        fontSize: "28px",
        fontWeight: "700",
        color: "#f1f5f9",
        margin: "0 0 4px 0",
    },
    subtitle: {
        fontSize: "14px",
        color: "#64748b",
        margin: 0,
    },
    card: {
        background: "#1e293b",
        border: "1px solid #334155",
        borderRadius: "16px",
        overflow: "hidden",
    },
    tableHeader: {
        display: "grid",
        gridTemplateColumns: "1fr 2fr 140px",
        padding: "12px 20px",
        background: "#0f172a",
        borderBottom: "1px solid #334155",
        fontSize: "11px",
        fontWeight: "600",
        letterSpacing: "0.08em",
        color: "#64748b",
        textTransform: "uppercase",
    },
    tableRow: {
        display: "grid",
        gridTemplateColumns: "1fr 2fr 140px",
        padding: "16px 20px",
        borderBottom: "1px solid #1e3048",
        alignItems: "center",
        transition: "background 0.15s",
    },
    avatar: {
        width: "34px",
        height: "34px",
        borderRadius: "50%",
        background: "linear-gradient(135deg, #3b82f6, #8b5cf6)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: "700",
        fontSize: "13px",
        color: "#fff",
        marginRight: "10px",
        flexShrink: 0,
    },
    nameCell: {
        display: "flex",
        alignItems: "center",
    },
    nameText: {
        fontSize: "14px",
        fontWeight: "600",
        color: "#f1f5f9",
    },
    emailText: {
        fontSize: "14px",
        color: "#94a3b8",
    },
    sendBtn: {
        background: "linear-gradient(135deg, #3b82f6, #6366f1)",
        color: "#fff",
        border: "none",
        borderRadius: "8px",
        padding: "7px 14px",
        fontSize: "12px",
        fontWeight: "600",
        cursor: "pointer",
        display: "flex",
        alignItems: "center",
        gap: "6px",
        transition: "opacity 0.15s, transform 0.1s",
    },
    emptyState: {
        padding: "60px 20px",
        textAlign: "center",
        color: "#475569",
    },
    // Add User form
    formCard: {
        background: "#1e293b",
        border: "1px solid #334155",
        borderRadius: "16px",
        padding: "24px",
        marginBottom: "24px",
    },
    formTitle: {
        fontSize: "16px",
        fontWeight: "700",
        color: "#f1f5f9",
        margin: "0 0 4px 0",
    },
    formSubtitle: {
        fontSize: "13px",
        color: "#64748b",
        margin: "0 0 20px 0",
    },
    formGrid: {
        display: "grid",
        gridTemplateColumns: "1fr 1fr 1fr auto",
        gap: "12px",
        alignItems: "end",
    },
    formField: {
        display: "flex",
        flexDirection: "column",
        gap: "6px",
    },
    addBtn: {
        padding: "10px 20px",
        background: "linear-gradient(135deg, #10b981, #059669)",
        color: "#fff",
        border: "none",
        borderRadius: "10px",
        fontSize: "13px",
        fontWeight: "700",
        cursor: "pointer",
        whiteSpace: "nowrap",
        transition: "opacity 0.2s",
        height: "40px",
    },
    // Modal
    backdrop: {
        position: "fixed",
        inset: 0,
        background: "rgba(0,0,0,0.6)",
        backdropFilter: "blur(4px)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
    },
    modal: {
        background: "#1e293b",
        border: "1px solid #334155",
        borderRadius: "20px",
        padding: "28px",
        width: "100%",
        maxWidth: "440px",
        boxShadow: "0 25px 60px rgba(0,0,0,0.5)",
        position: "relative",
    },
    modalTitle: {
        fontSize: "18px",
        fontWeight: "700",
        color: "#f1f5f9",
        margin: "0 0 4px 0",
    },
    modalSubtitle: {
        fontSize: "13px",
        color: "#64748b",
        margin: "0 0 24px 0",
    },
    closeBtn: {
        position: "absolute",
        top: "16px",
        right: "16px",
        background: "#334155",
        border: "none",
        color: "#94a3b8",
        width: "30px",
        height: "30px",
        borderRadius: "50%",
        cursor: "pointer",
        fontSize: "16px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
    },
    label: {
        display: "block",
        fontSize: "12px",
        fontWeight: "600",
        color: "#94a3b8",
        marginBottom: "6px",
        letterSpacing: "0.05em",
        textTransform: "uppercase",
    },
    input: {
        width: "100%",
        background: "#0f172a",
        border: "1px solid #334155",
        borderRadius: "10px",
        padding: "10px 14px",
        color: "#f1f5f9",
        fontSize: "14px",
        outline: "none",
        boxSizing: "border-box",
        marginBottom: "16px",
        transition: "border-color 0.2s",
    },
    textarea: {
        width: "100%",
        background: "#0f172a",
        border: "1px solid #334155",
        borderRadius: "10px",
        padding: "10px 14px",
        color: "#f1f5f9",
        fontSize: "14px",
        outline: "none",
        boxSizing: "border-box",
        resize: "vertical",
        minHeight: "110px",
        marginBottom: "20px",
        fontFamily: "inherit",
        transition: "border-color 0.2s",
    },
    submitBtn: {
        width: "100%",
        padding: "12px",
        background: "linear-gradient(135deg, #3b82f6, #6366f1)",
        color: "#fff",
        border: "none",
        borderRadius: "10px",
        fontSize: "14px",
        fontWeight: "700",
        cursor: "pointer",
        transition: "opacity 0.2s",
    },
    statusSuccess: {
        marginTop: "12px",
        padding: "10px 14px",
        background: "#064e3b",
        border: "1px solid #059669",
        borderRadius: "8px",
        color: "#34d399",
        fontSize: "13px",
        textAlign: "center",
    },
    statusError: {
        marginTop: "12px",
        padding: "10px 14px",
        background: "#450a0a",
        border: "1px solid #dc2626",
        borderRadius: "8px",
        color: "#f87171",
        fontSize: "13px",
        textAlign: "center",
    },
};

function AddUserForm({ onUserAdded }) {
    const [form, setForm] = useState({ name: "", email: "", password: "" });
    const [status, setStatus] = useState(null); // null | "adding" | "added" | "error"
    const [errorMsg, setErrorMsg] = useState("");

    const handleChange = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

    const handleAdd = async () => {
        if (!form.name.trim() || !form.email.trim() || !form.password.trim()) {
            setErrorMsg("All fields are required.");
            setStatus("error");
            return;
        }
        setStatus("adding");
        setErrorMsg("");
        try {
            await addUser({ name: form.name, email: form.email, password: form.password });
            setForm({ name: "", email: "", password: "" });
            setStatus("added");
            onUserAdded();
            setTimeout(() => setStatus(null), 3000);
        } catch (error) {
            setErrorMsg(error?.response?.data?.message || "Failed to add user.");
            setStatus("error");
        }
    };

    return (
        <div style={styles.formCard}>
            <p style={styles.formTitle}>➕ Add New User</p>
            <p style={styles.formSubtitle}>Register a new user to the system</p>
            <div style={styles.formGrid}>
                <div style={styles.formField}>
                    <label style={styles.label}>Name</label>
                    <input
                        style={{ ...styles.input, marginBottom: 0 }}
                        type="text"
                        placeholder="John Doe"
                        value={form.name}
                        onChange={handleChange("name")}
                    />
                </div>
                <div style={styles.formField}>
                    <label style={styles.label}>Email</label>
                    <input
                        style={{ ...styles.input, marginBottom: 0 }}
                        type="email"
                        placeholder="john@example.com"
                        value={form.email}
                        onChange={handleChange("email")}
                    />
                </div>
                <div style={styles.formField}>
                    <label style={styles.label}>Password</label>
                    <input
                        style={{ ...styles.input, marginBottom: 0 }}
                        type="password"
                        placeholder="••••••••"
                        value={form.password}
                        onChange={handleChange("password")}
                    />
                </div>
                <button
                    style={{ ...styles.addBtn, opacity: status === "adding" ? 0.6 : 1 }}
                    onClick={handleAdd}
                    disabled={status === "adding"}
                    onMouseEnter={(e) => (e.target.style.opacity = "0.8")}
                    onMouseLeave={(e) => (e.target.style.opacity = status === "adding" ? "0.6" : "1")}
                >
                    {status === "adding" ? "Adding…" : "Add User"}
                </button>
            </div>
            {status === "added" && <div style={{ ...styles.statusSuccess, marginTop: "14px" }}>✓ User added successfully!</div>}
            {status === "error" && <div style={{ ...styles.statusError, marginTop: "14px" }}>✕ {errorMsg}</div>}
        </div>
    );
}

function MessageBox({ setMessageBox, email }) {
    const [subject, setSubject] = useState("");
    const [message, setMessage] = useState("");
    const [status, setStatus] = useState(null); // null | "sending" | "sent" | "error"

    const handleSend = async () => {
        if (!subject.trim() || !message.trim()) return;
        setStatus("sending");
        try {
            const response = await sendTestGmail({ email, subject, message });
            console.log(response);
            setStatus("sent");
        } catch (error) {
            console.log(error);
            setStatus("error");
        }
    };

    return (
        <div style={styles.backdrop} onClick={(e) => e.target === e.currentTarget && setMessageBox(false)}>
            <div style={styles.modal}>
                <button style={styles.closeBtn} onClick={() => setMessageBox(false)}>✕</button>
                <p style={styles.modalTitle}>Send Test Email</p>
                <p style={styles.modalSubtitle}>To: {email}</p>

                <label style={styles.label}>Subject</label>
                <input
                    style={styles.input}
                    type="text"
                    placeholder="Enter subject..."
                    value={subject}
                    onChange={(e) => setSubject(e.target.value)}
                />

                <label style={styles.label}>Message</label>
                <textarea
                    style={styles.textarea}
                    placeholder="Write your message..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                />

                <button
                    style={{ ...styles.submitBtn, opacity: status === "sending" ? 0.6 : 1 }}
                    onClick={handleSend}
                    disabled={status === "sending"}
                >
                    {status === "sending" ? "Sending…" : "Send Email"}
                </button>

                {status === "sent" && <div style={styles.statusSuccess}>✓ Email sent successfully!</div>}
                {status === "error" && <div style={styles.statusError}>✕ Failed to send. Check console.</div>}
            </div>
        </div>
    );
}

export default function TestPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [messageBox, setMessageBox] = useState(false);
    const [email, setEmail] = useState("");

    const fetchUsers = () => {
        setLoading(true);
        getUsers()
            .then((res) => setUsers(res.data))
            .catch(console.error)
            .finally(() => setLoading(false));
    };

    useEffect(() => { fetchUsers(); }, []);

    const openModal = (email) => {
        setEmail(email);
        setMessageBox(true);
    };

    return (
        <div style={styles.page}>
            <div style={styles.header}>
                <h1 style={styles.title}>🛠 Test Panel</h1>
                <p style={styles.subtitle}>Send test emails to registered users</p>
            </div>

            <AddUserForm onUserAdded={fetchUsers} />

            <div style={styles.card}>
                <div style={styles.tableHeader}>
                    <span>Name</span>
                    <span>Email</span>
                    <span>Action</span>
                </div>

                {loading ? (
                    <div style={styles.emptyState}>Loading users…</div>
                ) : users.length === 0 ? (
                    <div style={styles.emptyState}>No users found.</div>
                ) : (
                    users.map((user, i) => (
                        <div
                            key={user.email}
                            style={{
                                ...styles.tableRow,
                                background: i % 2 === 0 ? "transparent" : "#172033",
                            }}
                        >
                            <div style={styles.nameCell}>
                                <div style={styles.avatar}>
                                    {(user.name || user.email)[0].toUpperCase()}
                                </div>
                                <span style={styles.nameText}>{user.name || "—"}</span>
                            </div>
                            <span style={styles.emailText}>{user.email}</span>
                            <button
                                style={styles.sendBtn}
                                onClick={() => openModal(user.email)}
                                onMouseEnter={(e) => (e.target.style.opacity = "0.8")}
                                onMouseLeave={(e) => (e.target.style.opacity = "1")}
                            >
                                ✉ Send Email
                            </button>
                        </div>
                    ))
                )}
            </div>

            {messageBox && <MessageBox setMessageBox={setMessageBox} email={email} />}
        </div>
    );
}