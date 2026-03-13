import { axiosInstance } from "ikon-react-components-lib";
import type { ConnectorConfig, ConnectorConfigRequest } from "./types";

const BASE_PATH =
  (import.meta.env.VITE_CONNECTOR_API_BASE_URL as string | undefined) ?? "/connector/configs";

export async function fetchConnectorConfigs(): Promise<ConnectorConfig[]> {
  const { data } = await axiosInstance.get<ConnectorConfig[]>(BASE_PATH);
  return data;
}

export async function fetchConnectorConfigById(id: string): Promise<ConnectorConfig> {
  const { data } = await axiosInstance.get<ConnectorConfig>(`${BASE_PATH}/${id}`);
  return data;
}

export async function createConnectorConfig(
  request: ConnectorConfigRequest
): Promise<ConnectorConfig> {
  const { data } = await axiosInstance.post<ConnectorConfig>(BASE_PATH, request);
  return data;
}

export async function updateConnectorConfig(
  id: string,
  request: ConnectorConfigRequest
): Promise<ConnectorConfig> {
  const { data } = await axiosInstance.put<ConnectorConfig>(`${BASE_PATH}/${id}`, request);
  return data;
}

export async function deleteConnectorConfig(id: string): Promise<void> {
  await axiosInstance.delete(`${BASE_PATH}/${id}`);
}
