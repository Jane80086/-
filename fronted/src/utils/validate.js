export function isEmail(email) {
  return /\S+@\S+\.\S+/.test(email)
}
export function isPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
} 