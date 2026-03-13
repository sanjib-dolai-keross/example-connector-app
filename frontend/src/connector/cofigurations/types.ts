export interface ConnectorConfig {
  id: string;
  sourceApplication: string;
  sourceModule: string;
  endpointUrl: string;
  httpMethod: string;
  isExternal: boolean;
  transportProtocol: string;
  dataRootPath?: string;
  paginationConfig?: {
    type?: string;
    path?: string;
    limitParam?: string;
    batchSize?: number;
  };
  authConfig?: {
    strategy: string;
    authHeaderName?: string;
    authHeaderPrefix?: string;
    injectLocation?: string;
    queryParamName?: string;
    encodeBase64?: boolean;
    encryptionAlgorithm?: string;
    grantType?: string;
    tokenResponsePath?: string;
    expiryResponsePath?: string;
    signatureHeaderName?: string;
    includeTimestamp?: boolean;
    additionalHeaders?: Record<string, string>;
  };
  fieldMappings?: Array<Record<string, object>>;
  createdOn?: string;
  updatedOn?: string;
}

export type ConnectorConfigRequest = Omit<ConnectorConfig, "id" | "createdOn" | "updatedOn">;
