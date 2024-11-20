import styled from "styled-components";

const StyledImg = styled.div`
  background-image: url(${(props) => props.src || "https://via.placeholder.com/293x358"});
  background-size: cover;
  background-position: center;
`;

function Img({ src }) {
  return <StyledImg src={src}></StyledImg>;
}

export default Img;
