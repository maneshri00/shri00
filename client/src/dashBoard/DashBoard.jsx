import { useEffect, useState } from "react";
import CreateAccountForm from "./CreateAccountForm";
import { useNavigate } from "react-router-dom";

function DashBoard() {
  const [isNewUser, setIsNewUser] = useState(null);
  const [workspace, setWorkspace] = useState(null);
  const navigate = useNavigate();

 useEffect(() => {
  fetch("http://localhost:8080/api/user/status", {
    credentials: "include",
  })
    .then(res => res.json())
    .then(data => {
      setIsNewUser(data.isNewUser);
      setWorkspace(data.workspaceAllowed || null);
      if (!data.isNewUser) {
        const path = (data.workspaceAllowed === "professional") ? "/professional" : "/personal";
        navigate(path);
      }
    })
    .catch(() => setIsNewUser(false)); // safety
}, []);


  if (isNewUser === null) return <p>Loading...</p>;

  return (
    <>
      {isNewUser ? <CreateAccountForm /> : null}
    </>
  );
}

export default DashBoard;
