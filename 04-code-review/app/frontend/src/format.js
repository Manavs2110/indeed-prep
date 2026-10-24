export function formatSalary(salary) {
  return salary == null ? 'Salary not listed' : `$${salary.toLocaleString()}`;
}
