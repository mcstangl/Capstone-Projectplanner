export class CredentialsDto {
  private _loginName: string
  private _password: string

  constructor(loginName: string, password: string) {
    this._loginName = loginName
    this._password = password
  }

  get loginName(): string {
    return this._loginName
  }

  set loginName(value: string) {
    this._loginName = value
  }

  get password(): string {
    return this._password
  }

  set password(value: string) {
    this._password = value
  }
}
