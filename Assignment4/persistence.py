import sqlite3
import atexit
from dbtools import Dao
 
# Data Transfer Objects:
class Employee(object):
    #TODO: implement 
    def __init__(self,id,name,salary,branche):
        self.id=id
        self.name=name
        self.salary=salary
        self.branche=branche
          
    
 
class Supplier(object):
    #TODO: implement
    def __init__(self,id,name,contact_information):
        self.id=id
        self.name=name
        self.contact_information=contact_information
        

class Product(object):
    #TODO: implement
    def __init__(self,id,description,price,quantity):
        self.id=id
        self.description=description
        self.price=price
        self.quantity=quantity

class Branche(object):
    #TODO: implement
    def __init__(self,id,location,number_of_employees):
        self.id=id
        self.location=location
        self.number_of_employees=number_of_employees

class Activitie(object):
    #TODO: implement
    def __init__(self,product_id,quantity,activator_id,date):
        self.product_id=product_id
        self.quantity=quantity
        self.activator_id=activator_id
        self.date=date
 
 
#Repository
class Repository(object):
    def __init__(self):
        self._conn = sqlite3.connect('bgumart.db')
        # self._conn.text_factory = bytes
        #TODO: complete
        self.employees=Dao(Employee,self._conn)
        self.suppliers=Dao(Supplier,self._conn)
        self.products=Dao(Product,self._conn)
        self.branches=Dao(Branche,self._conn)
        self.activities=Dao(Activitie,self._conn)

 
    def _close(self):
        self._conn.commit()
        self._conn.close()
 
    def create_tables(self):
        self._conn.executescript("""
            CREATE TABLE employees (
                id              INT         PRIMARY KEY,
                name            TEXT        NOT NULL,
                salary          REAL        NOT NULL,
                branche    INT REFERENCES branches(id)
            );
    
            CREATE TABLE suppliers (
                id                   INTEGER    PRIMARY KEY,
                name                 TEXT       NOT NULL,
                contact_information  TEXT
            );

            CREATE TABLE products (
                id          INTEGER PRIMARY KEY,
                description TEXT    NOT NULL,
                price       REAL NOT NULL,
                quantity    INTEGER NOT NULL
            );

            CREATE TABLE branches (
                id                  INTEGER     PRIMARY KEY,
                location            TEXT        NOT NULL,
                number_of_employees INTEGER
            );
    
            CREATE TABLE activities (
                product_id      INTEGER REFERENCES products(id),
                quantity        INTEGER NOT NULL,
                activator_id    INTEGER NOT NULL,
                date            TEXT    NOT NULL
            );
        """)

    def execute_command(self, script: str) -> list:
        return self._conn.cursor().execute(script).fetchall()
    
    def get_all_employees(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM employees ORDER BY employees.id""")
        all = cursor.fetchall()
        return all

    def get_all_suppliers(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM suppliers ORDER BY suppliers.id""")
        all = cursor.fetchall()
        return all
    
    def get_all_products(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM products ORDER BY products.id""")
        all = cursor.fetchall()
        return all
    
    def get_all_branches(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM branches ORDER BY branches.id """)
        all = cursor.fetchall()
        return all

    def get_all_activities(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM activities ORDER BY activities.date""")
        all = cursor.fetchall()
        return all
    
    def add_activitie(self,splittedline : list[str]):
        productid=splittedline[0]
        quantity=splittedline[1]
        intquant=int(quantity)
        activatorid=splittedline[2]
        date=splittedline[3]
        cursor = self._conn.cursor()
        cursor.execute("""SELECT quantity FROM products WHERE id=?""",[productid,])
        one=cursor.fetchone()[0]
        if(intquant>0 or (intquant<0 and one>=intquant*-1)):
            activitieObject=Activitie(splittedline[0],splittedline[1],splittedline[2],splittedline[3])
            repo.activities.insert(activitieObject)
            newquant=one+intquant
            cursor.execute("UPDATE products SET quantity = ? WHERE id = ?",[newquant,productid,])
            
                
    def employees_report(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT employees.name, employees.salary, branches.location, SUM(COALESCE(products.price*(-1*activities.quantity),0)) as total_sales_income 
        FROM employees JOIN branches ON employees.branche = branches.id
        LEFT JOIN activities ON employees.id = activities.activator_id
        LEFT JOIN products ON activities.product_id = products.id
        GROUP BY employees.name, employees.salary,branches.location
        ORDER BY employees.name""")
        all=cursor.fetchall()
        return all
    def activities_report(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT activities.date, products.description, activities.quantity,
        sellers.name as seller_name, suppliers.name as supplier_name
        FROM activities
        JOIN products ON products.id = activities.product_id
        LEFT JOIN employees AS sellers ON sellers.id = activities.activator_id
        LEFT JOIN suppliers ON suppliers.id = activities.activator_id
        ORDER BY activities.date
        """)
        all=cursor.fetchall()
        return all

# singleton
repo = Repository()
atexit.register(repo._close)