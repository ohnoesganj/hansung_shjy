import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import Header from "./Header";
import "./DiaryDetail.css";
import { useCookies } from "react-cookie";

function DiaryDetail() {
  const [cookies] = useCookies(["couple_id", "user_id", "nickname"]);
  const couple_id = cookies.couple_id;
  const user_id = cookies.user_id;
  const [diarydate, setDiaryDate] = useState(Date);
  const [usercontent, setUserContent] = useState("");
  const [othercontent, setOtherContent] = useState("");
  const [userNN, setUserNN] = useState("");
  const [otherNN, setOtherNN] = useState("");
  const [userID1, setUserID1] = useState("");
  const [userID2, setUserID2] = useState("");
  const encodedNickname = decodeURIComponent(cookies.nickname);
  const [file, setFile] = useState(null);
  const thumbnailInput = useRef();
  const [imageSrc, setImageSrc] = useState("");

  const onDiaryDate = (e) => {
    setDiaryDate(e.target.value);
  };
  const onDiaryUser = (e) => {
    setUserContent(e.target.value);
  };
  const onDiaryOther = (e) => {
    setOtherContent(e.target.value);
  };

  const encodeFileToBase64 = (fileBlob) => {
    const reader = new FileReader();
    const formData = new FormData();
    const uploadFile = fileBlob;
    console.log(uploadFile);
    formData.append("file", uploadFile);
    setFile(uploadFile);
    if (fileBlob) {
      reader.readAsDataURL(fileBlob);
    }

    return new Promise((resolve) => {
      reader.onload = () => {
        setImageSrc(reader.result);

        resolve();
      };
    });
  };

  useEffect(() => {
    axios
      .get(`http://localhost:3000/diary/detail`, {
        params: {
          couple_id: couple_id,
        },
      })
      .then((res) => {
        console.log(JSON.stringify(res.data) + "::res");

        setUserNN(res.data.nickname1);
        setOtherNN(res.data.nickname2);
        setUserID1(res.data.userID1);
        setUserID2(res.data.userID2);
      })
      .catch((err) => {
        console.log(err + "::err");
      });
  }, [couple_id]);

  const DiarySave = (e) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("diaryDate", diarydate);
    formData.append("myDiary", usercontent);
    formData.append("otherDiary", othercontent);
    formData.append("userID", user_id);

    axios
      .post(`http://localhost:3000/diary/save/${couple_id}`, formData)
      .then((response) => {
        console.log(response);
        document.location.href = "/diary";
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <div>
      <Header />

      <div>
        <button className="diarysave_btn" onClick={(e) => DiarySave(e)}>
          일기 등록
        </button>
        <div className="diary_div">
          <h4 className="diarydate_lb">오늘의 날짜</h4>
          <input
            type="date"
            className="diarydate_ip"
            value={diarydate}
            onChange={onDiaryDate}
          />
        </div>

        <h4 className="diaryphoto_lb">사진 등록</h4>
        <form
          className="file_upload"
          name="form"
          method="post"
          encType="multipart/form-data"
        >
          {imageSrc && <img height="100px" src={imageSrc} alt="preview-img" />}
          <br />
          <input
            type="file"
            name="image"
            accept="image/*"
            multiple
            ref={thumbnailInput}
            onChange={(e) => {
              encodeFileToBase64(e.target.files[0]);
            }}
          />
        </form>
        <br />
        <h4 className="diary_lb">오늘의 일기</h4>
        <label className="DiaryD_lb">
          {" "}
          {decodeURIComponent(encodedNickname)} 님의 기록
        </label>
        <label className="DiaryD1_lb">
          {encodedNickname === userNN ? otherNN : userNN} 님의 기록
        </label>
        <br />
        <textarea
          className="diaryuser_txt"
          placeholder="일기는 수정할 수 없으니 등록하기 전에 확인 바랍니다."
          value={usercontent}
          onChange={onDiaryUser}
        />

        <textarea
          disabled
          className="diaryother_txt"
          placeholder="일기는 수정할 수 없으니 등록하기 전에 확인 바랍니다."
          value={othercontent}
          onChange={onDiaryOther}
        />
      </div>
    </div>
  );
}

export default DiaryDetail;
