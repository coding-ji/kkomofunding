import React, { useState } from "react";
import styled from "styled-components";

const CarouselContainer = styled.div`
  display: flex;
  flex-direction : column;
  align-items: center;
  gap: 20px;
  align-items: flex-end;
  width:100%;
`;

const MainImage = styled.img`
  width: 100%;
  height: 650px;
  object-fit: cover;
  border-radius: 2px;
  
`;

const SideContainer = styled.div`
  display: flex;
  width : 100%;
  gap: 10px;
`;

const ThumbnailContainer = styled.div`
  display: flex;
  width: 100%;
  height : 150px;
  gap: 10px;
  overflow-y: auto;
  max-height: auto;
  scrollbar-width: thin;
  scrollbar-color: #ddd #f4f4f4;
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 3px;
  }
  &::-webkit-scrollbar-track {
    background-color: #f4f4f4;
  }
`;

const Thumbnail = styled.img`
  // width: 100%;
  // height: 100%;
  object-fit: cover;
  border-radius: 2px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  border: ${(props) => (props.isSelected ? "2px solid #007BFF" : "none")};
  // box-shadow: ${(props) =>  props.isSelected ? "0px 0px 6px rgba(0, 123, 255, 0.5)" : "none"};
  transition: transform 0.5s ease; /* 부드러운 확대/축소 전환 */

`;

const NavButton = styled.button`
  width: 31px; /* 썸네일과 동일한 너비 */
  font-size: 16px;
  cursor: pointer;
  border: 2px solid #256E91;
  border-radius: 2px;
  // background-color: #f4f4f4;
  color : #256E91;

  &:hover {
    background-color: #e0e0e0;
  }
`;

function ImageCarousel({ images }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  const changeImage = (index) => {
    if (index < 0) setCurrentIndex(images.length - 1);
    else if (index >= images.length) setCurrentIndex(0);
    else setCurrentIndex(index);
  };

  const handlePrev = () => changeImage(currentIndex - 1);
  const handleNext = () => changeImage(currentIndex + 1);
  const handleThumbnailClick = (index) => changeImage(index);

  // 이미지가 없는 경우 처리
  if (!images || images.length === 0) {
    return <div>No images available</div>;
  }

  return (
    <CarouselContainer>
      <MainImage src={images[currentIndex]} alt={`Image ${currentIndex + 1}`} />
      <SideContainer>
        <NavButton onClick={handlePrev} aria-label="Previous image">◀</NavButton>
        <ThumbnailContainer>
          {images.map((image, index) => (
            <Thumbnail
              key={index}
              id={`thumbnail-${index}`}
              src={image}
              alt={`Thumbnail ${index + 1}`}
              onClick={() => handleThumbnailClick(index)}
              isSelected={currentIndex === index}
            />
          ))}
        </ThumbnailContainer>
        <NavButton onClick={handleNext} aria-label="Next image">▶</NavButton>
      </SideContainer>
    </CarouselContainer>
  );
}

export default ImageCarousel;
