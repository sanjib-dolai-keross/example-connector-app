import { ProviderWrapper, setIkonConfig } from "ikon-react-components-lib";
import ConfigPage from "./connector/cofigurations/ConfigPage";

setIkonConfig({
  IKON_BASE_API_URL: "https://ikoncloud-dev.keross.com/ikon-api",
  IKON_PLATFORM_UI_URL: "/",
  LOGIN_PAGE_URL: "/login.html",
});

function App() {
  return (
    <ProviderWrapper>
      <ConfigPage />
    </ProviderWrapper>
  );
}

export default App;
