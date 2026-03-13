import { Button } from "ikon-react-components-lib";

export default function ConfigPage() {
  return (
    <div className="p-6 space-y-6">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Connector Configurations</h1>
        <div>
          <Button>Create New</Button>
        </div>
      </header>
    </div>
  );
}
